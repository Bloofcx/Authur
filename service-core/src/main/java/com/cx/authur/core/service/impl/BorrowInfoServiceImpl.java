package com.cx.authur.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cx.common.exception.Assert;
import com.cx.common.result.ResponseEnum;
import com.cx.authur.core.BorrowInfoStatusEnum;
import com.cx.authur.core.BorrowerStatusEnum;
import com.cx.authur.core.UserBindEnum;
import com.cx.authur.core.mapper.BorrowInfoMapper;
import com.cx.authur.core.mapper.LendMapper;
import com.cx.authur.core.pojo.entity.BorrowInfo;
import com.cx.authur.core.pojo.entity.UserInfo;
import com.cx.authur.core.pojo.vo.BorrowInfoApprovalVO;
import com.cx.authur.core.pojo.vo.BorrowerDetailVO;
import com.cx.authur.core.service.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 借款信息表 服务实现类
 * </p>
 *
 * @author ChenXu
 * @since 2022-02-07
 */
@Service
public class BorrowInfoServiceImpl extends ServiceImpl<BorrowInfoMapper, BorrowInfo> implements BorrowInfoService {

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private BorrowInfoMapper borrowInfoMapper;

    @Resource
    private BorrowerService borrowerService;

    @Resource
    private LendMapper lendMapper;

    @Resource
    private DictService dictService;

    @Resource
    private LendService lendService;

    @Override
    public void saveBorrowInfo(BorrowInfo borrowInfo, Long userId) {
        UserInfo userInfo = userInfoService.getById(userId);
        //判定用户绑定状态
        Assert.isTrue(userInfo.getBindStatus() == UserBindEnum.BIND_OK.getStatus().intValue(), ResponseEnum.USER_NO_BIND_ERROR);
        //判断借款人申请状态
        Assert.isTrue(userInfo.getBorrowAuthStatus() == BorrowerStatusEnum.AUTH_OK.getStatus().intValue(),ResponseEnum.USER_NO_AMOUNT_ERROR);


        //判断额度是否足够
        Assert.isTrue(borrowInfo.getAmount().doubleValue() <= userInfoService.getBorrowAmountByUserId(userId).doubleValue(),ResponseEnum.USER_AMOUNT_LESS_ERROR);

        borrowInfo.setUserId(userId);
        borrowInfo.setBorrowYearRate(borrowInfo.getBorrowYearRate().divide(new BigDecimal(100)));
        borrowInfo.setStatus(BorrowInfoStatusEnum.CHECK_RUN.getStatus());
        baseMapper.insert(borrowInfo);


    }

    @Override
    public Integer getBorrowStatus(Long userId) {
        QueryWrapper<BorrowInfo> borrowInfoQueryWrapper = new QueryWrapper<>();
        borrowInfoQueryWrapper.select("status").eq("user_id",userId);
        List<Object> objects = baseMapper.selectObjs(borrowInfoQueryWrapper);
        if (objects.size() == 0){
            return BorrowInfoStatusEnum.NO_AUTH.getStatus();
        }
        return (Integer) objects.get(0);
    }

    @Override
    public List<BorrowInfo> getBorrowInfoList() {
        List<BorrowInfo> list = borrowInfoMapper.getList();
        for (BorrowInfo borrowInfo : list) {
            String returnMethod = dictService.getNameByValAndType(borrowInfo.getReturnMethod(),"returnMethod");
            String moneyUse = dictService.getNameByValAndType(borrowInfo.getMoneyUse(), "moneyUse");
            String status = BorrowInfoStatusEnum.getMsgByStatus(borrowInfo.getStatus());
            Map<String,Object> map = new HashMap<>();
            map.put("returnMethod",returnMethod);
            map.put("moneyUse",moneyUse);
            map.put("status",status);
            borrowInfo.setParam(map);
        }
        return list;
    }

    @Override
    public Map<String, Object> getBorrowInfoById(Long id) {
        Map<String,Object> result = new HashMap<>();
        BorrowInfo borrowInfo = borrowInfoMapper.selectById(id);
        String returnMethod = dictService.getNameByValAndType(borrowInfo.getReturnMethod(),"returnMethod");
        String moneyUse = dictService.getNameByValAndType(borrowInfo.getMoneyUse(), "moneyUse");
        String status = BorrowInfoStatusEnum.getMsgByStatus(borrowInfo.getStatus());
        Map<String,Object> map = new HashMap<>();
        map.put("returnMethod",returnMethod);
        map.put("moneyUse",moneyUse);
        map.put("status",status);
        borrowInfo.setParam(map);
        result.put("borrowInfo",borrowInfo);
        BorrowerDetailVO borrowerDetailVO = borrowerService.getBorrowerById(borrowInfo.getUserId());
        result.put("borrower",borrowerDetailVO);
        return result;
    }

    @Override
    public void approvlBorrowInfo(BorrowInfoApprovalVO borrowInfoApprovalVO) {
        BorrowInfo borrowInfo = borrowInfoMapper.selectById(borrowInfoApprovalVO.getId());
        borrowInfo.setStatus(borrowInfoApprovalVO.getStatus());
        borrowInfoMapper.updateById(borrowInfo);

        if (borrowInfoApprovalVO.getStatus() == BorrowInfoStatusEnum.CHECK_OK.getStatus().intValue()){
//            Lend lend = new Lend();
//            TODO
//            lendMapper.insert()
            lendService.createLend(borrowInfoApprovalVO,borrowInfo);
        }
    }
}
