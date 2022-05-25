package com.cx.authur.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cx.common.exception.Assert;
import com.cx.common.result.ResponseEnum;
import com.cx.common.util.MD5;
import com.cx.authur.base.util.JwtUtils;
import com.cx.authur.core.mapper.IntegralGradeMapper;
import com.cx.authur.core.mapper.UserAccountMapper;
import com.cx.authur.core.mapper.UserInfoMapper;
import com.cx.authur.core.mapper.UserLoginRecordMapper;
import com.cx.authur.core.pojo.entity.IntegralGrade;
import com.cx.authur.core.pojo.entity.UserAccount;
import com.cx.authur.core.pojo.entity.UserInfo;
import com.cx.authur.core.pojo.entity.UserLoginRecord;
import com.cx.authur.core.pojo.query.UserinfoQuery;
import com.cx.authur.core.pojo.vo.LoginVO;
import com.cx.authur.core.pojo.vo.RegisterVO;
import com.cx.authur.core.pojo.vo.UserinfoVO;
import com.cx.authur.core.service.UserInfoService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 用户基本信息 服务实现类
 * </p>
 *
 * @author ChenXu
 * @since 2022-02-07
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {
    @Resource
    UserLoginRecordMapper userLoginRecordMapper;

    @Resource
    private UserAccountMapper userAccountMapper;

    @Resource
    private IntegralGradeMapper integralGradeMapper;

    private final String USER_KEY = "user-login-lim";
    private final int INTERVAL = 60;
    private final int MAX_COUNT = 1;

    @Resource
    private RedisTemplate redisTemplate;


    // 是否超过最大限制
    private boolean isOverMaxCount(String userKey) {
        String redisKey = USER_KEY + userKey;

        Long curTime = System.currentTimeMillis();

        // 统计滑动窗口内的个数
        Long count =  redisTemplate.opsForZSet().count(redisKey,curTime - INTERVAL * 1000,curTime);
        if (count >= MAX_COUNT) {
            return true;
        } else {
            return false;
        }
    }

    public boolean canAccess(String userKey) {
        if (isOverMaxCount(userKey)) {
            return false;
        } else {
            increment(userKey);
            return true;
        }
    }
    // 滑动窗口计数增长
    private void increment(String userKey) {
        String redisKey = USER_KEY + userKey;
        ZSetOperations zSetOperations = redisTemplate.opsForZSet();
        long  curTime = System.currentTimeMillis();

        // 清除窗口过期成员
        zSetOperations.removeRangeByScore(redisKey,0,curTime - INTERVAL * 1000);

        // 添加计数
        zSetOperations.add(redisKey,String.valueOf(curTime),curTime);

        //expire
        redisTemplate.expire(redisKey,INTERVAL, TimeUnit.SECONDS);
    }

    @Transactional
    @Override
    public UserinfoVO login(LoginVO loginVO,String ip) {
        //判断用户是否存在
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("mobile",loginVO.getMobile())
                .eq("user_type",loginVO.getUserType());
        UserInfo userInfo = baseMapper.selectOne(queryWrapper);
        Assert.notNull(userInfo,ResponseEnum.LOGIN_MOBILE_ERROR);
        //判断用户密码是否正确
        Assert.isTrue(MD5.encrypt(loginVO.getPassword()).equals(userInfo.getPassword()),ResponseEnum.LOGIN_PASSWORD_ERROR);

      // 滑动窗口限流
        Assert.isTrue(canAccess(String.valueOf(userInfo.getId())),ResponseEnum.LOGIN_LIMITER);


        //生成登录日志
        UserLoginRecord userLoginRecord = new UserLoginRecord();
        userLoginRecord.setUserId(userInfo.getId());
        userLoginRecord.setIp(ip);
        userLoginRecordMapper.insert(userLoginRecord);

        //生成JWT
        String jwt = JwtUtils.createToken(userInfo.getId(),userInfo.getName());
        //封装userInfoVO对象
        UserinfoVO userinfoVO = new UserinfoVO();
        userinfoVO.setUserType(loginVO.getUserType());
        userinfoVO.setMobile(loginVO.getMobile());
        userinfoVO.setName(userInfo.getName());
        userinfoVO.setNickName(userInfo.getNickName());
        userinfoVO.setHeadImg(userInfo.getHeadImg());

        userinfoVO.setJwt(jwt);
        return userinfoVO;
    }

    @Override
    public Page<UserInfo> listByCondition(UserinfoQuery userinfoQuery, Page<UserInfo> userInfoPage) {
        if (userinfoQuery == null){
           return baseMapper.selectPage(userInfoPage, null);
        }

        String mobile = userinfoQuery.getMobile();
        Integer userType = userinfoQuery.getUserType();
        Integer status = userinfoQuery.getStatus();
        QueryWrapper<UserInfo> userInfoQueryWrapper = new QueryWrapper<>();
        userInfoQueryWrapper.eq(StringUtils.isNotBlank(mobile),"mobile",mobile);
        userInfoQueryWrapper.eq(userType != null,"user_type",userType);
        userInfoQueryWrapper.eq(status!=null,"status",status);
        Page<UserInfo> pageInfo = baseMapper.selectPage(userInfoPage, userInfoQueryWrapper);
        return pageInfo;
    }

    @Override
    public void lock(Long id, Integer status) {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(id);
        userInfo.setStatus(status);
        baseMapper.updateById(userInfo);
    }

    @Override
    public boolean checkMobileExist(String mobile) {
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("mobile",mobile);
        Integer count = baseMapper.selectCount(queryWrapper);
        if (count > 0){
            return true;
        }else {
            return false;
        }

    }

    /**
     * 根据用户id获取用户借款额度
     * @param userId
     * @return
     */
    @Override
    public BigDecimal getBorrowAmountByUserId(Long userId) {
        UserInfo userInfo = baseMapper.selectById(userId);
        Assert.notNull(userInfo, ResponseEnum.LOGIN_MOBILE_ERROR);
        QueryWrapper<IntegralGrade> integralGradeQueryWrapper = new QueryWrapper<>();
        integralGradeQueryWrapper.ge("integral_end",userInfo.getIntegral())
                        .le("integral_start",userInfo.getIntegral());
        IntegralGrade integralGrade = integralGradeMapper.selectOne(integralGradeQueryWrapper);
        if (integralGrade == null){
            return new BigDecimal(0);
        }
        return integralGrade.getBorrowAmount();
    }


    @Transactional
    @Override
    public void register(RegisterVO registerVO) {
        //判断手机号是否已经存在？
        QueryWrapper<UserInfo> userInfoQueryWrapper = new QueryWrapper<>();
        userInfoQueryWrapper.eq("mobile",registerVO.getMobile());
        userInfoQueryWrapper.eq("user_type",registerVO.getUserType());
        Integer count = baseMapper.selectCount(userInfoQueryWrapper);
        Assert.isTrue(count == 0, ResponseEnum.MOBILE_EXIST_ERROR);

//        if (count > 0){
//            throw new BusinessException(ResponseEnum.MOBILE_EXIST_ERROR);
//        }

        //生成用户信息
        UserInfo userInfo = new UserInfo();
        userInfo.setUserType(registerVO.getUserType());
        userInfo.setMobile(registerVO.getMobile());
        userInfo.setName(registerVO.getMobile());
        userInfo.setNickName(registerVO.getMobile());
        userInfo.setHeadImg("https://elden.oss-cn-beijing.aliyuncs.com/authur/55710535_p0_master1200.jpg");
        userInfo.setPassword(MD5.encrypt(registerVO.getPassword()));
        baseMapper.insert(userInfo);

        //生成用户账户信息
        UserAccount userAccount = new UserAccount();
        userAccount.setUserId(userInfo.getId());
        userAccountMapper.insert(userAccount);

    }
}
