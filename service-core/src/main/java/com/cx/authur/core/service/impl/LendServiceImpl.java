package com.cx.authur.core.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cx.common.exception.BusinessException;
import com.cx.authur.core.LendStatusEnum;
import com.cx.authur.core.ReturnMethodEnum;
import com.cx.authur.core.TransTypeEnum;
import com.cx.authur.core.hfb.HfbConst;
import com.cx.authur.core.hfb.RequestHelper;
import com.cx.authur.core.mapper.LendMapper;
import com.cx.authur.core.mapper.UserAccountMapper;
import com.cx.authur.core.mapper.UserInfoMapper;
import com.cx.authur.core.pojo.bo.TransFlowBO;
import com.cx.authur.core.pojo.entity.*;
import com.cx.authur.core.pojo.vo.BorrowInfoApprovalVO;
import com.cx.authur.core.pojo.vo.BorrowerDetailVO;
import com.cx.authur.core.service.*;
import com.cx.authur.core.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * 标的准备表 服务实现类
 * </p>
 *
 * @author ChenXu
 * @since 2022-02-07
 */
@Service
@Slf4j
public class LendServiceImpl extends ServiceImpl<LendMapper, Lend> implements LendService {

    @Resource
    private LendReturnService lendReturnService;

    @Resource
    private LendItemReturnService lendItemReturnService;

    @Resource
    private DictService dictService;

    @Resource
    private BorrowerService borrowerService;

    @Resource
    private LendItemService lendItemService;

    @Resource
    private UserInfoMapper userInfoMapper;

    @Resource
    private UserAccountMapper userAccountMapper;

    @Resource
    private TransFlowService transFlowService;


    @Resource
    private Redisson redisson;

    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public Map<String,Object> getLendById(Long id) {
        QueryWrapper<Lend> lendQueryWrapper = new QueryWrapper<>();
        lendQueryWrapper.eq("id",id);
        Lend lend = baseMapper.selectOne(lendQueryWrapper);

        //组装Lend
        HashMap<String, Object> map = new HashMap<>();
        String returnMethod = dictService.getNameByValAndType(lend.getReturnMethod(),"returnMethod");
        String status = LendStatusEnum.getMsgByStatus(lend.getStatus());
        map.put("returnMethod",returnMethod);
        map.put("status",status);
        lend.setParam(map);
        //组装Borrower

        BorrowerDetailVO borrowerDetailVO = borrowerService.getBorrowerById(lend.getUserId());
        HashMap<String, Object> result = new HashMap<>();
        result.put("lend",lend);
        result.put("borrower",borrowerDetailVO);
        return result;
    }

    @Override
    public void createLend(BorrowInfoApprovalVO borrowInfoApprovalVO, BorrowInfo borrowInfo) {
        Lend lend = new Lend();
        lend.setUserId(borrowInfo.getUserId());

        lend.setBorrowInfoId(borrowInfo.getId());

        lend.setLendNo(LendNoUtils.getLendNo());//生成编号

        lend.setTitle(borrowInfoApprovalVO.getTitle());

        lend.setAmount(borrowInfo.getAmount());

        lend.setPeriod(borrowInfo.getPeriod());

        lend.setLendYearRate(borrowInfoApprovalVO.getLendYearRate().divide(new BigDecimal(100)));//从审批对象中获取

        lend.setServiceRate(borrowInfoApprovalVO.getServiceRate().divide(new BigDecimal(100)));//从审批对象中获取

        lend.setReturnMethod(borrowInfo.getReturnMethod());

        lend.setLowestAmount(new BigDecimal(100));

        lend.setInvestAmount(new BigDecimal(0));

        lend.setInvestNum(0);

        lend.setPublishDate(LocalDateTime.now());
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startDate = LocalDate.parse(borrowInfoApprovalVO.getLendStartDate(),dtf);
        lend.setLendStartDate(startDate);

        lend.setLendEndDate(startDate.plusMonths(borrowInfo.getPeriod()));

       //平台预期收益

        //        月年化 = 年化 / 12

        BigDecimal monthRate = lend.getServiceRate().divide(new BigDecimal(12), 8, BigDecimal.ROUND_DOWN);

        //        平台收益 = 标的金额 * 月年化 * 期数

        BigDecimal expectAmount = lend.getAmount().multiply(monthRate).multiply(new BigDecimal(lend.getPeriod()));

        lend.setExpectAmount(expectAmount);

        //实际收益

        lend.setRealAmount(new BigDecimal(0));

        //状态

        lend.setStatus(LendStatusEnum.INVEST_RUN.getStatus());

        //审核时间

        lend.setCheckTime(LocalDateTime.now());

        //审核人

        lend.setCheckAdminId(1L);

        baseMapper.insert(lend);
    }

    @Override
    public List<Lend> selectList() {
        RLock readLock = null;
        RLock writeLock = null;
        try {
             readLock = redisson.getReadWriteLock("lendList").readLock();
             writeLock = redisson.getReadWriteLock("lendList").writeLock();
             readLock.lock();

             if (redisTemplate.opsForValue().get("lend-list") != null) {
                 return (List<Lend>) redisTemplate.opsForValue().get("lend-list");
             } else {
                 writeLock.lock();
                 List<Lend> lends = baseMapper.selectList(null);
                 lends.forEach(lend -> {
                     HashMap<String, Object> map = new HashMap<>();
                     String returnMethod = dictService.getNameByValAndType(lend.getReturnMethod(),"returnMethod");
                     String status = LendStatusEnum.getMsgByStatus(lend.getStatus());
                     map.put("returnMethod",returnMethod);
                     map.put("status",status);
                     lend.setParam(map);
                 });
                 redisTemplate.opsForValue().set("lend-list",lends,5, TimeUnit.MINUTES);
                 return lends;
             }
        } finally {
            readLock.unlock();
            writeLock.unlock();
        }

    }

    @Override
    public BigDecimal getInterestCount(BigDecimal invest, BigDecimal yearRate, Integer totalMonth, Integer returnMethod) {
        BigDecimal interestCount = null;
        if (returnMethod.intValue() == ReturnMethodEnum.ONE.getMethod()) {
            interestCount = Amount1Helper.getInterestCount(invest, yearRate, totalMonth);
        }else if (returnMethod.intValue() == ReturnMethodEnum.TWO.getMethod()) {

            interestCount = Amount2Helper.getInterestCount(invest, yearRate, totalMonth);

        } else if(returnMethod.intValue() == ReturnMethodEnum.THREE.getMethod()) {

            interestCount = Amount3Helper.getInterestCount(invest, yearRate, totalMonth);

        } else {

            interestCount = Amount4Helper.getInterestCount(invest, yearRate, totalMonth);
        }

        return interestCount;
    }

    /**
     * 满标放款
     * @param lendId
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void makeLoan(Long lendId) {
        //获取标的信息
        Lend lend = baseMapper.selectById(lendId);

        //放款接口调用
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("agentId", HfbConst.AGENT_ID);
        paramMap.put("agentProjectCode", lend.getLendNo());//标的编号
        String agentBillNo = LendNoUtils.getLoanNo();//放款编号
        paramMap.put("agentBillNo", agentBillNo);

        //平台收益，放款扣除，借款人借款实际金额=借款金额-平台收益
        //月年化
        BigDecimal monthRate = lend.getServiceRate().divide(new BigDecimal(12), 8, BigDecimal.ROUND_DOWN);

        //平台实际收益 = 已投金额 * 月年化 * 标的期数
        BigDecimal realAmount = lend.getInvestAmount().multiply(monthRate).multiply(new BigDecimal(lend.getPeriod()));
        paramMap.put("mchFee", realAmount); //商户手续费(平台实际收益)
        paramMap.put("timestamp", RequestHelper.getTimestamp());
        String sign = RequestHelper.getSign(paramMap);
        paramMap.put("sign", sign);

        //发送同步远程调用
        JSONObject result = RequestHelper.sendRequest(paramMap, HfbConst.MAKE_LOAD_URL);
        log.info("放款结果：" + result.toJSONString());

        //放款失败
        if (!"0000".equals(result.getString("resultCode"))) {
            throw new BusinessException(result.getString("resultMsg"));
        }

        //更新标的信息
        lend.setRealAmount(realAmount);
        lend.setStatus(LendStatusEnum.PAY_RUN.getStatus());
        lend.setPaymentTime(LocalDateTime.now());
        baseMapper.updateById(lend);

        //获取借款人信息
        Long userId = lend.getUserId();
        UserInfo userInfo = userInfoMapper.selectById(userId);
        String bindCode = userInfo.getBindCode();


        //给借款账号转入金额
        BigDecimal total = new BigDecimal(result.getString("voteAmt"));
        userAccountMapper.updateUserAccount(total,new BigDecimal("0"),bindCode);


        //新增借款人交易流水
        TransFlowBO transFlowBO = new TransFlowBO(
                agentBillNo,
                bindCode,
                total,
                TransTypeEnum.BORROW_BACK,
                "借款放款到账，编号：" + lend.getLendNo());//项目编号
        transFlowService.saveTransFlow(transFlowBO);



        //获取投资列表信息
        List<LendItem> lendItemList = lendItemService.selectByLendId(lendId, 1);
        lendItemList.forEach(item -> {
            //获取投资人信息
            Long investUserId = item.getInvestUserId();
            UserInfo investUserInfo = userInfoMapper.selectById(investUserId);
            String investBindCode = investUserInfo.getBindCode();


            //投资人账号冻结金额转出
            BigDecimal investAmount = item.getInvestAmount(); //投资金额
            userAccountMapper.updateUserAccount(new BigDecimal("0"),investAmount.negate(),investBindCode);

            //新增投资人交易流水
            TransFlowBO investTransFlowBO = new TransFlowBO(
                    LendNoUtils.getTransNo(),
                    investBindCode,
                    investAmount,
                    TransTypeEnum.INVEST_UNLOCK,
                    "冻结资金转出，出借放款，编号：" + lend.getLendNo());//项目编号
            transFlowService.saveTransFlow(investTransFlowBO);

            //放款成功生成借款人还款计划和投资人回款计划
            this.repaymentPlan(lend);
        });
    }

    /**
     * 回款计划
     * @param lendItemId
     * @param lendReturnMap
     * @param lend
     * @return
     */
    @Override
    public List<LendItemReturn> returnInvest(Long lendItemId, Map<Integer, Long> lendReturnMap, Lend lend) {

        //获取当前投资记录信息
        LendItem lendItem = lendItemService.getById(lendItemId);

        //调用工具类计算还款本金和利息，存储为集合
        // {key：value}
        // {期数：本金|利息}
        BigDecimal amount = lendItem.getInvestAmount();
        BigDecimal yearRate = lendItem.getLendYearRate();
        Integer totalMonth = lend.getPeriod();

        Map<Integer, BigDecimal> mapInterest = null;  //还款期数 -> 利息
        Map<Integer, BigDecimal> mapPrincipal = null; //还款期数 -> 本金

        //根据还款方式计算本金和利息
        if (lend.getReturnMethod().intValue() == ReturnMethodEnum.ONE.getMethod()) {
            //利息
            mapInterest = Amount1Helper.getPerMonthInterest(amount, yearRate, totalMonth);
            //本金
            mapPrincipal = Amount1Helper.getPerMonthPrincipal(amount, yearRate, totalMonth);
        } else if (lend.getReturnMethod().intValue() == ReturnMethodEnum.TWO.getMethod()) {
            mapInterest = Amount2Helper.getPerMonthInterest(amount, yearRate, totalMonth);
            mapPrincipal = Amount2Helper.getPerMonthPrincipal(amount, yearRate, totalMonth);
        } else if (lend.getReturnMethod().intValue() == ReturnMethodEnum.THREE.getMethod()) {
            mapInterest = Amount3Helper.getPerMonthInterest(amount, yearRate, totalMonth);
            mapPrincipal = Amount3Helper.getPerMonthPrincipal(amount, yearRate, totalMonth);
        } else {
            mapInterest = Amount4Helper.getPerMonthInterest(amount, yearRate, totalMonth);
            mapPrincipal = Amount4Helper.getPerMonthPrincipal(amount, yearRate, totalMonth);
        }

        //创建回款计划列表
        List<LendItemReturn> lendItemReturnList = new ArrayList<>();

        for (Map.Entry<Integer, BigDecimal> entry : mapInterest.entrySet()) {
            Integer currentPeriod = entry.getKey();//当前期数
            // 根据当前期数，获取还款计划的id
            Long lendReturnId = lendReturnMap.get(currentPeriod);

            //创建回款计划记录
            LendItemReturn lendItemReturn = new LendItemReturn();
            //将还款记录关联到回款记录
            lendItemReturn.setLendReturnId(lendReturnId);
            //设置回款记录的基本属性
            lendItemReturn.setLendItemId(lendItemId);
            lendItemReturn.setInvestUserId(lendItem.getInvestUserId());
            lendItemReturn.setLendId(lendItem.getLendId());
            lendItemReturn.setInvestAmount(lendItem.getInvestAmount());
            lendItemReturn.setLendYearRate(lend.getLendYearRate());
            lendItemReturn.setCurrentPeriod(currentPeriod);
            lendItemReturn.setReturnMethod(lend.getReturnMethod());

            //计算回款本金、利息和总额（注意最后一个月的计算）
            if(currentPeriod.intValue() == lend.getPeriod().intValue()){//最后一期
                //本金
                BigDecimal sumPrincipal = lendItemReturnList.stream()
                        .map(LendItemReturn::getPrincipal)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal lastPrincipal = lendItem.getInvestAmount().subtract(sumPrincipal);
                lendItemReturn.setPrincipal(lastPrincipal);

                //利息
                BigDecimal sumInterest = lendItemReturnList.stream()
                        .map(LendItemReturn::getInterest)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal lastInterest = lendItem.getExpectAmount().subtract(sumInterest);
                lendItemReturn.setInterest(lastInterest);

            }else{//非最后一期
                //本金
                lendItemReturn.setPrincipal(mapPrincipal.get(currentPeriod));
                //利息
                lendItemReturn.setInterest(mapInterest.get(currentPeriod));
            }

            //回款总金额
            lendItemReturn.setTotal(lendItemReturn.getPrincipal().add(lendItemReturn.getInterest()));

            //设置回款状态和是否逾期等其他属性
            lendItemReturn.setFee(new BigDecimal("0"));
            lendItemReturn.setReturnDate(lend.getLendStartDate().plusMonths(currentPeriod));
            lendItemReturn.setOverdue(false);
            lendItemReturn.setStatus(0);

            //将回款记录放入回款列表
            lendItemReturnList.add(lendItemReturn);
        }

        //批量保存
        lendItemReturnService.saveBatch(lendItemReturnList);

        return lendItemReturnList;
    }

    /**
     * 生成还款计划
     * @param lend
     */
    private void repaymentPlan(Lend lend) {
        //还款计划列表
        List<LendReturn> lendReturnList = new ArrayList<>();

        //按还款时间生成还款计划
        int len = lend.getPeriod().intValue();


        for (int i = 1; i <= len; i++) {

            //创建还款计划对象
            LendReturn lendReturn = new LendReturn();
            lendReturn.setReturnNo(LendNoUtils.getReturnNo());
            lendReturn.setLendId(lend.getId());
            lendReturn.setBorrowInfoId(lend.getBorrowInfoId());
            lendReturn.setUserId(lend.getUserId());
            lendReturn.setAmount(lend.getAmount());
            lendReturn.setBaseAmount(lend.getInvestAmount());
            lendReturn.setLendYearRate(lend.getLendYearRate());
            lendReturn.setCurrentPeriod(i);//当前期数
            lendReturn.setReturnMethod(lend.getReturnMethod());


            //说明：还款计划中的这三项 = 回款计划中对应的这三项和：因此需要先生成对应的回款计划
            //          lendReturn.setPrincipal();
            //          lendReturn.setInterest();
            //          lendReturn.setTotal();

            lendReturn.setFee(new BigDecimal(0));
            lendReturn.setReturnDate(lend.getLendStartDate().plusMonths(i)); //第二个月开始还款
            lendReturn.setOverdue(false);
            if (i == len) { //最后一个月
                //标识为最后一次还款
                lendReturn.setLast(true);
            } else {
                lendReturn.setLast(false);
            }
            lendReturn.setStatus(0);
            lendReturnList.add(lendReturn);
        }

         //批量保存
        lendReturnService.saveBatch(lendReturnList);

        //获取lendReturnList中还款期数与还款计划id对应map
        //期数 - lendId
        Map<Integer, Long> lendReturnMap = lendReturnList.stream().collect(
                Collectors.toMap(LendReturn::getCurrentPeriod, LendReturn::getId)
        );


        //======================================================
        //=============获取所有投资者，生成回款计划===================
        //======================================================
        //

        List<LendItemReturn> lendItemReturnAllList = new ArrayList<>();
        //获取投资成功的投资记录
        List<LendItem> lendItemList = lendItemService.selectByLendId(lend.getId(), 1);
        for (LendItem lendItem : lendItemList) {
            //创建回款计划列表
            List<LendItemReturn> lendItemReturnList = this.returnInvest(lendItem.getId(), lendReturnMap, lend);
            lendItemReturnAllList.addAll(lendItemReturnList);
        }


        //更新还款计划中的相关金额数据
        for (LendReturn lendReturn : lendReturnList) {
            BigDecimal sumPrincipal = lendItemReturnAllList.stream()
            //过滤条件：当回款计划中的还款计划id == 当前还款计划id的时候
                    .filter(item -> item.getLendReturnId().longValue() == lendReturn.getId().longValue())
            //将所有回款计划中计算的每月应收本金相加
                    .map(LendItemReturn::getPrincipal)

                    .reduce(BigDecimal.ZERO, BigDecimal::add);


            BigDecimal sumInterest = lendItemReturnAllList.stream()

                    .filter(item -> item.getLendReturnId().longValue() == lendReturn.getId().longValue())

                    .map(LendItemReturn::getInterest)

                    .reduce(BigDecimal.ZERO, BigDecimal::add);


            BigDecimal sumTotal = lendItemReturnAllList.stream()

                    .filter(item -> item.getLendReturnId().longValue() == lendReturn.getId().longValue())

                    .map(LendItemReturn::getTotal)

                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            lendReturn.setPrincipal(sumPrincipal); //每期还款本金
            lendReturn.setInterest(sumInterest); //每期还款利息
            lendReturn.setTotal(sumTotal); //每期还款本息
        }
        lendReturnService.updateBatchById(lendReturnList);
    }


    }

