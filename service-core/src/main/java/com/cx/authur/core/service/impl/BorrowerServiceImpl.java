package com.cx.authur.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cx.authur.core.BorrowerStatusEnum;
import com.cx.authur.core.IntegralEnum;
import com.cx.authur.core.mapper.BorrowerMapper;
import com.cx.authur.core.mapper.UserInfoMapper;
import com.cx.authur.core.mapper.UserIntegralMapper;
import com.cx.authur.core.pojo.entity.Borrower;
import com.cx.authur.core.pojo.entity.BorrowerAttach;
import com.cx.authur.core.pojo.entity.UserInfo;
import com.cx.authur.core.pojo.entity.UserIntegral;
import com.cx.authur.core.pojo.vo.BorrowerApprovalVO;
import com.cx.authur.core.pojo.vo.BorrowerAttachVO;
import com.cx.authur.core.pojo.vo.BorrowerDetailVO;
import com.cx.authur.core.pojo.vo.BorrowerVO;
import com.cx.authur.core.service.BorrowerAttachService;
import com.cx.authur.core.service.BorrowerService;
import com.cx.authur.core.service.DictService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 借款人 服务实现类
 * </p>
 *
 * @author ChenXu
 * @since 2022-02-07
 */
@Service
public class BorrowerServiceImpl extends ServiceImpl<BorrowerMapper, Borrower> implements BorrowerService {

    @Resource
    private BorrowerMapper borrowerMapper;

    @Resource
    private UserInfoMapper userInfoMapper;

    @Resource
    private BorrowerAttachService borrowerAttachService;

    @Resource
    private DictService dictService;

    @Resource
    private UserIntegralMapper userIntegralMapper;


    @Override
    @Transactional
    public void saveBorrowerVOByUserId(BorrowerVO borrowerVO, Long userId) {
        UserInfo userInfo = userInfoMapper.selectById(userId);

        //添加借款人信息
        Borrower borrower = new Borrower();
        BeanUtils.copyProperties(borrowerVO, borrower);
        borrower.setStatus(BorrowerStatusEnum.AUTH_RUN.getStatus());
        borrower.setUserId(userId);
        borrower.setName(userInfo.getName());
        borrower.setIdCard(userInfo.getIdCard());
        borrower.setMobile(userInfo.getMobile());
        borrowerMapper.insert(borrower);

        //添加借款人附件信息

        List<BorrowerAttach> borrowerAttachList = borrowerVO.getBorrowerAttachList();
        borrowerAttachService.insertByBorrowerAttachList(borrowerAttachList,borrower);


        //修改用户表的借款状态
        userInfo.setBorrowAuthStatus(BorrowerStatusEnum.AUTH_RUN.getStatus());
        userInfoMapper.updateById(userInfo);
    }

    /**
     * 获取借款人状态信息
     *
     * @param userId
     * @return
     */
    @Override
    public Integer getStatusByUserId(Long userId) {
        QueryWrapper<Borrower> borrowerQueryWrapper = new QueryWrapper<>();

        borrowerQueryWrapper.select("status").eq("user_id", userId);

        List<Object> objects = baseMapper.selectObjs(borrowerQueryWrapper);


        if (objects.size() == 0) {

            //借款人尚未提交信息

            return BorrowerStatusEnum.NO_AUTH.getStatus();

        }

        Integer status = (Integer) objects.get(0);

        return status;
    }

    /**
     * 获取所有借款人记录信息
     * @param borrowerPage
     * @param keyword
     * @return
     */
    @Override
    public IPage<Borrower> listPage(Page<Borrower> borrowerPage, String keyword) {
        if (StringUtils.isBlank(keyword)){
            return baseMapper.selectPage(borrowerPage,null);
        }
        QueryWrapper<Borrower> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("name",keyword)
                .or().like("id_card",keyword)
                .or().like("mobile",keyword);
        return baseMapper.selectPage(borrowerPage,queryWrapper);
    }

    @Override
    public BorrowerDetailVO getBorrowerById(Long userId) {

        QueryWrapper<Borrower> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        Borrower borrower = borrowerMapper.selectOne(queryWrapper);
        if (borrower == null) return null;
        BorrowerDetailVO borrowerDetailVO = new BorrowerDetailVO();

        //根据borrower封装borrowerVO
        BeanUtils.copyProperties(borrower,borrowerDetailVO);
        borrowerDetailVO.setSex(borrower.getSex()== 1?"男":"女");
        borrowerDetailVO.setMarry(borrower.getMarry() ? "已婚":"未婚");
        borrowerDetailVO.setIndustry(transfer(borrower.getIndustry(),"industry"));
        borrowerDetailVO.setIncome(transfer(borrower.getIncome(),"income"));
        borrowerDetailVO.setReturnSource(transfer(borrower.getReturnSource(),"returnSource"));
        borrowerDetailVO.setEducation(transfer(borrower.getEducation(),"education"));
        borrowerDetailVO.setContactsRelation(transfer(borrower.getContactsRelation(),"relation"));
        borrowerDetailVO.setStatus(BorrowerStatusEnum.getMsgByStatus(borrower.getStatus()));

        //根据borrowerAttach表来封装AttachList
        List<BorrowerAttach> borrowerAttaches = borrowerAttachService.selectListByUserId(borrower.getId());
        List<BorrowerAttachVO> borrowerAttachVOS = new ArrayList<>();
        for (BorrowerAttach borrowerAttach : borrowerAttaches) {
            BorrowerAttachVO borrowerAttachVO = new BorrowerAttachVO();
            borrowerAttachVO.setImageType(borrowerAttach.getImageType());
            borrowerAttachVO.setImageUrl(borrowerAttach.getImageUrl());
            borrowerAttachVOS.add(borrowerAttachVO);
            log.debug(borrowerAttachVO.toString());
        }


        borrowerDetailVO.setBorrowerAttachVOList(borrowerAttachVOS);
        return borrowerDetailVO;
    }

    @Override
    public void dealBorrowerApproval(BorrowerApprovalVO borrowerApprovalVO) {
        //修改borrower表中的状态
        Long userId = borrowerApprovalVO.getBorrowerId(); //userId
        QueryWrapper<Borrower> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        Borrower borrower = borrowerMapper.selectOne(queryWrapper);
        borrower.setStatus(borrowerApprovalVO.getStatus());
        borrowerMapper.updateById(borrower);


        UserIntegral userIntegral = new UserIntegral();
        userIntegral.setUserId(borrower.getUserId());
        userIntegral.setContent("借款人基本信息");
        userIntegral.setIntegral(borrowerApprovalVO.getInfoIntegral());
        userIntegralMapper.insert(userIntegral);

        UserInfo userInfo = userInfoMapper.selectById(borrower.getUserId());
        int curIntegral = userInfo.getIntegral() + borrowerApprovalVO.getInfoIntegral();

        if (borrowerApprovalVO.getIsHouseOk()){
            //计算积分
             userIntegral = new UserIntegral();
            userIntegral.setUserId(borrower.getUserId());
            userIntegral.setContent(IntegralEnum.BORROWER_HOUSE.getMsg());
            curIntegral += IntegralEnum.BORROWER_HOUSE.getIntegral();
            userIntegral.setIntegral(curIntegral);
            userIntegralMapper.insert(userIntegral);
        }

        if (borrowerApprovalVO.getIsCarOk()){
            //计算积分
             userIntegral = new UserIntegral();
            userIntegral.setUserId(borrower.getUserId());
            userIntegral.setContent(IntegralEnum.BORROWER_CAR.getMsg());
            curIntegral += IntegralEnum.BORROWER_CAR.getIntegral();
            userIntegral.setIntegral(curIntegral);
            userIntegralMapper.insert(userIntegral);
        }

        if (borrowerApprovalVO.getIsIdCardOk()){
            //计算积分
             userIntegral = new UserIntegral();
            userIntegral.setUserId(borrower.getUserId());
            userIntegral.setContent(IntegralEnum.BORROWER_IDCARD.getMsg());
            curIntegral += IntegralEnum.BORROWER_IDCARD.getIntegral();
            userIntegral.setIntegral(curIntegral);
            userIntegralMapper.insert(userIntegral);
        }


        //修改用户表中的状态
       userInfo.setBorrowAuthStatus(borrowerApprovalVO.getStatus());
        userInfo.setIntegral(curIntegral);
        userInfoMapper.updateById(userInfo);


    }

    private String transfer(Integer value,String type){
        return dictService.getNameByValAndType(value, type);
    }
}
