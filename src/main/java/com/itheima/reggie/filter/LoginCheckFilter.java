package com.itheima.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Console;
import java.io.IOException;

/**
 * 检查用户是否完成网路
 */
@WebFilter(filterName = "LoginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    //因为有通配符，所以我们需要路径匹配器
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        //1.获取本次请求的URI
        String requestURI = request.getRequestURI();
        //2.看本次请求的URI是否需要处理
        String[] urls = new String[]{
          "/employee/login",
          "/employee/outlog",
          "/backend/**",
          "/front/**",
          "/user/login",
                "/user/**"
        };
        boolean check = check(urls, requestURI);
        //3.不需要处理，直接放行
        if(check){
            log.info("本次请求{}不需要处理",requestURI);
            filterChain.doFilter(request,response);
            return;
        }
        //4.判断登录状态，如果已登录，直接放行
        if(request.getSession().getAttribute("employee") != null){
            log.info("用户已登陆，用户id为",request.getSession().getAttribute("employee"));
            Long empId = (Long)request.getSession().getAttribute("employee");
            BaseContext.setId(empId);
            filterChain.doFilter(request,response);
            return;
        }
        if(request.getSession().getAttribute("phone") != null){
            log.info("用户已登陆，用户id为",request.getSession().getAttribute("phone"));
            Long empId = (Long)request.getSession().getAttribute("phone");
            BaseContext.setId(empId);
            filterChain.doFilter(request,response);
            return;
        }
        //5.如果未登录
        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    /**
     * 看本次请求的URI是否需要处理
     * @param urls
     * @param requestURI
     * @return
     */
    public boolean check(String[] urls , String requestURI){
        for (String url : urls) {
            if(PATH_MATCHER.match(url,requestURI)){
                return true;
            }
        }
        return false;
    }

}
