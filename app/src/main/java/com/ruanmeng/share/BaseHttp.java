package com.ruanmeng.share;

import com.ruanmeng.qiane_insurance.BuildConfig;

/**
 * 项目名称：Qiane_Insurance
 * 创建人：小卷毛
 * 创建时间：2018-06-20 10:39
 */
public class BaseHttp {

    private static String baseUrl = BuildConfig.API_HOST;
    private static String baseIp = baseUrl + "/api";
    public static String baseImg = baseUrl + "/";

    public static String register_sub = baseIp + "/register_sub.rm";                 //注册√
    public static String identify_get = baseIp + "/identify_get.rm";                 //注册验证码√
    public static String login_sub = baseIp + "/login_sub.rm";                       //登录√
    public static String identify_getbyforget = baseIp + "/identify_getbyforget.rm"; //忘记取验证码√
    public static String pwd_forget_sub = baseIp + "/pwd_forget_sub.rm";             //忘记密码√

    public static String user_msg_data = baseIp + "/user_msg_data.rm";                         //个人资料√
    public static String userinfo_uploadhead_sub = baseIp + "/userinfo_uploadhead_sub.rm";     //修改头像√
    public static String nickName_change_sub = baseIp + "/nickName_change_sub.rm";             //修改昵称√
    public static String sex_change_sub = baseIp + "/sex_change_sub.rm";                       //修改性别√
    public static String password_change_sub = baseIp + "/password_change_sub.rm";             //修改密码√
    public static String certification_sub = baseIp + "/certification_sub.rm";                 //实名认证√
    public static String user_profession_info = baseIp + "/user_profession_info.rm";           //职业信息√
    public static String add_profession_info = baseIp + "/add_profession_info.rm";             //修改职业信息√
    public static String user_business_card = baseIp + "/user_business_card.rm";               //名片信息√
    public static String userinfo_uploadWechat_sub = baseIp + "/userinfo_uploadWechat_sub.rm"; //上传二维码√
    public static String edit_company = baseIp + "/edit_company.rm";                           //更新公司√
    public static String assets_info = baseIp + "/assets_info.rm";                             //我的收入√
    public static String integral_info = baseIp + "/integral_info.rm";                         //我的积分√
    public static String withdraw_list_data = baseIp + "/withdraw_list_data.rm";               //提现列表√
    public static String profit_list_data = baseIp + "/profit_list_data.rm";                   //收益列表√
    public static String integral_list_data = baseIp + "/integral_list_data.rm";               //积分列表√
    public static String myprospectus_list_data = baseIp + "/myprospectus_list_data.rm";       //我的计划书√
    public static String goodsorder_list_data = baseIp + "/goodsorder_list_data.rm";           //订单列表√
    public static String find_usercustome_info = baseIp + "/find_usercustome_info.rm";         //客户列表√
    public static String find_usercustome_details = baseIp + "/find_usercustome_details.rm";   //客户信息√
    public static String add_usercustomer_sub = baseIp + "/add_usercustomer_sub.rm";           //添加客户√
    public static String edit_usercustomer_sub = baseIp + "/edit_usercustomer_sub.rm";         //修改客户√
    public static String delete_usercustomer_sub = baseIp + "/delete_usercustomer_sub.rm";     //删除客户√
    public static String find_usercustome_data = baseIp + "/find_usercustome_data.rm";         //搜索客户√

    public static String index_data = baseIp + "/index_data.rm";                           //首页√
    public static String index_productprospectus = baseIp + "/index_productprospectus.rm"; //首页列表√
    public static String prospectus_list_data = baseIp + "/prospectus_list_data.rm";       //计划书列表√
    public static String prospectus_detils = baseIp + "/prospectus_detils.rm";             //计划书详情√
    public static String cascadeItems_items_list = baseIp + "/cascadeItems_items_list.rm"; //获取级联√
    public static String insurancekind_detils = baseIp + "/insurancekind_detils.rm";       //险种信息√
    public static String add_app_prospectus = baseIp + "/add_app_prospectus.rm";           //新增计划书√
    public static String product_list_data = baseIp + "/product_list_data.rm";             //产品列表√
    public static String read_user_ctn_data = baseIp + "/read_user_ctn_data.rm";           //阅读数、获客数√
    public static String company_list_data = baseIp + "/company_list_data.rm";             //公司列表√
    public static String insurancetype_list_data = baseIp + "/insurancetype_list_data.rm"; //保险种类√
    public static String read_prospectus_info = baseIp + "/read_prospectus_info.rm";       //计划书阅读列表√

    public static String msg_list_data = baseIp + "/msg_list_data.rm";                 //资讯列表√
    public static String information_list_data = baseIp + "/information_list_data.rm"; //资讯列表√
    public static String information_detail = baseIp + "/information_detail.rm";       //资讯详情√
    public static String company_info = baseIp + "/company_info.rm";                   //公司详情√
    public static String leave_message_sub = baseIp + "/leave_message_sub.rm";         //意见反馈√
    public static String help_center = baseIp + "/help_center.rm";                     //帮助中心√
    public static String city_name_data = baseIp + "/city_name_data.rm";               //获取省市区√

    public static String invite_index = baseImg + "forend/invitation_index.hm?userInfoId=";                  //分享√
    public static String register_index = baseImg + "forend/register_index.hm?userInfoId=";                  //分享注册√
    public static String user_businessCard = baseImg + "forend/user_businessCard.hm?type=1&fromuserInfoId="; //分享名片√
    public static String businessCard = baseImg + "forend/user_businessCard.hm?fromuserInfoId=";             //我的名片√
    public static String prospectus_open = baseImg + "forend/prospectus_open.hm?prospectusId=";              //查看计划书√
    public static String prospectus_detil = baseImg + "forend/prospectus_detils.hm?type=1&prospectusId=";    //计划书详情√
    public static String product_detils = baseImg + "forend/product_detils.hm?productinId=";                 //产品详情√
    public static String share_product_detils = baseImg + "forend/share_product_detils.hm?productinId=";     //产品详情分享√
    public static String order_detlis = baseImg + "forend/goods_order_detlis.hm?goodsOrderId=";              //订单详情√
}
