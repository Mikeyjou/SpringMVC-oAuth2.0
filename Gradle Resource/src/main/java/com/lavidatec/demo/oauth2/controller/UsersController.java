/*
 * UsersAction
 * Author : Mikey-2017/09/10
 * 
 */
package com.lavidatec.demo.oauth2.controller;


import com.lavidatec.template.entity.TrainsModel;
import com.lavidatec.template.entity.UsersModel;
import com.lavidatec.template.pojo.PasswordHash;
import com.lavidatec.template.service.ITrainService;
import com.lavidatec.template.service.IUserService;
import com.lavidatec.template.service.TrainServiceImpl;
import com.lavidatec.template.service.UserServiceImpl;
import com.lavidatec.template.vo.TrainsVo;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import org.json.JSONObject;
import org.json.JSONArray;

@Controller
@RequestMapping("/User")
public class UsersController {
    
    /**
     * log init.
     */
    static final Logger LOGGER
            = LoggerFactory.getLogger(UsersController.class);
    
    private IUserService userService = new UserServiceImpl();
    private ITrainService trainService = new TrainServiceImpl();
    
    //取得HttpServletRequest的參數
    private Map<String,String> getParam(HttpServletRequest request) 
            throws IOException,
                   ArrayIndexOutOfBoundsException{        
        Map<String,String> result = new HashMap<>();
        String[] requestStr = IOUtils.toString(request.getReader()).split("&");
        for(String s: requestStr){
            String[] tmp = s.split("=");
            tmp[0] = URLDecoder.decode(tmp[0], "UTF-8");
            tmp[1] = URLDecoder.decode(tmp[1], "UTF-8");             
            result.put(tmp[0], tmp[1]);
        }
        return result;
    }
    
    //使用者註冊
    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> addUser(@RequestParam("account") String account, 
                          @RequestParam("password") String pwd)
            throws Exception{
        if(StringUtils.isNotBlank(account) && StringUtils.isNotBlank(pwd)){
            UsersModel userModel = new UsersModel();
            userModel.setAccount(account);
            userModel.setPassword(pwd);
            //確認使用者不存在
            if(!userService.userFind(Optional.of(userModel)).isPresent()){
                userService.userPersist(Optional.of(userModel));
                return new ResponseEntity<>(new ApiResponse(HttpStatus.OK,"Success",null), HttpStatus.OK);
            }else{
                return new ResponseEntity<>(new ApiResponse(HttpStatus.BAD_REQUEST,"This username is duplicate",null), HttpStatus.BAD_REQUEST);
            }
        }else{
            return new ResponseEntity<>(new ApiResponse(HttpStatus.BAD_REQUEST,"Miss some parameters",null), HttpStatus.BAD_REQUEST);
        }
    }
    
    //使用者帳號刪除
    @RequestMapping(value = "", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<Object> deleteUser(HttpServletRequest request)
            throws Exception{
        try{
            Map<String,String> paramMap = getParam(request);

            String account = paramMap.get("account");
            String pwd = paramMap.get("password");
            if(StringUtils.isNotBlank(account) && StringUtils.isNotBlank(pwd)){
                UsersModel userModel = new UsersModel();
                userModel.setAccount(account);
                userModel.setPassword(pwd);
                //確認使用者存在
                if(userService.userFind(Optional.of(userModel)).isPresent()){
                    userService.userRemove(Optional.of(userModel));
                    return new ResponseEntity<>(new ApiResponse(HttpStatus.OK,"Success",null), HttpStatus.OK);
                }else{
                    return new ResponseEntity<>(new ApiResponse(HttpStatus.NOT_FOUND,"Username or password is wrong",null), HttpStatus.NOT_FOUND);
                }
            }else{
                return new ResponseEntity<>(new ApiResponse(HttpStatus.BAD_REQUEST,"Miss some parameters",null), HttpStatus.BAD_REQUEST);    
            }
        }catch(ArrayIndexOutOfBoundsException e){
            return new ResponseEntity<>(new ApiResponse(HttpStatus.BAD_REQUEST,"Miss some parameters",null), HttpStatus.BAD_REQUEST);
        }
    }
    
    //使用者登入
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> loginUser(@RequestParam("account") String account, 
                           @RequestParam("password") String pwd)
            throws Exception{
        if(StringUtils.isNotBlank(account) && StringUtils.isNotBlank(pwd)){
            UsersModel userModel = new UsersModel();
            userModel.setAccount(account);
            userModel.setPassword(pwd);
            //確認使用者存在
            Optional<UsersModel> user = userService.userFind(Optional.of(userModel));
            if(user.isPresent()){
                JSONObject result = new JSONObject();
                result.put("Identifier", userService.userLogin(user));
                return new ResponseEntity<>(new ApiResponse(HttpStatus.OK,"Success",new JSONArray().put(result)), HttpStatus.OK);
            }else{
                return new ResponseEntity<>(new ApiResponse(HttpStatus.NOT_FOUND,"Username or password is wrong",null), HttpStatus.NOT_FOUND);
            }
        }else{
            return new ResponseEntity<>(new ApiResponse(HttpStatus.BAD_REQUEST,"Miss some parameters",null), HttpStatus.BAD_REQUEST);
        }
    }   
    
    //使用者登出
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> logoutUser(@RequestParam("account") String account, 
                            @RequestParam("password") String pwd)
            throws Exception{
        if(StringUtils.isNotBlank(account) && StringUtils.isNotBlank(pwd)){
            UsersModel userModel = new UsersModel();
            userModel.setAccount(account);
            userModel.setPassword(pwd);
            //確認使用者存在
            if(userService.userFind(Optional.of(userModel)).isPresent()){
                userService.userLogout(Optional.of(userModel));
                return new ResponseEntity<>(new ApiResponse(HttpStatus.OK,"Success",null), HttpStatus.OK);
            }else{
                return new ResponseEntity<>(new ApiResponse(HttpStatus.NOT_FOUND,"Username or password is wrong",null), HttpStatus.NOT_FOUND);
            }
        }else{
            return new ResponseEntity<>(new ApiResponse(HttpStatus.BAD_REQUEST,"Miss some parameters",null), HttpStatus.BAD_REQUEST);
        }
    }
    
    //訂票
    @RequestMapping(value = "/order", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> bookTicket(@RequestParam("account") String account, 
                            @RequestParam("password") String pwd,
                            @RequestParam("identifier") String identifier,
                            @RequestParam("no") String no)
            throws Exception{
        if(StringUtils.isNotBlank(account) && StringUtils.isNotBlank(pwd) && StringUtils.isNotBlank(identifier) && StringUtils.isNotBlank(no)){
            //確認該車存在
            TrainsVo trainVo = new TrainsVo();
            trainVo.setNo(no);
            Optional<TrainsModel> train = trainService.trainFind(trainVo);
            if(!train.isPresent()){
                return new ResponseEntity<>(new ApiResponse(HttpStatus.NOT_FOUND,"Cant find this train",null), HttpStatus.NOT_FOUND);
            }else{
                if(train.get().getTicketsLimit() <= 0)
                    return new ResponseEntity<>(new ApiResponse(HttpStatus.NO_CONTENT,"No tickets anymore",null), HttpStatus.NO_CONTENT);
            }
            UsersModel userModel = new UsersModel();
            userModel.setAccount(account);
            userModel.setPassword(pwd);
            //確認使用者存在
            Optional<UsersModel> user = userService.userFind(Optional.of(userModel));
            if(!user.isPresent()){
                return new ResponseEntity<>(new ApiResponse(HttpStatus.NOT_FOUND,"Username or password is wrong",null), HttpStatus.NOT_FOUND);
            }
            //確認使用者辨識碼正確
            userModel.setIdentifier(identifier);
            user = userService.userFind(Optional.of(userModel));
            if(!user.isPresent()){
                return new ResponseEntity<>(new ApiResponse(HttpStatus.BAD_REQUEST,"This identifier is wrong",null), HttpStatus.BAD_REQUEST);
            }
            //確認Optimistic Lock
            System.out.println("User controller OptLock" + user.get().getOptimisticLock());
            String orderToken = userService.userBook(user, no);
            if(orderToken != ""){
                JSONObject result = new JSONObject();
                result.put("Order token", orderToken);
                return new ResponseEntity<>(new ApiResponse(HttpStatus.OK,"Success",new JSONArray().put(result)), HttpStatus.OK);
            }else{
                return new ResponseEntity<>(new ApiResponse(HttpStatus.BAD_REQUEST,"Book failed",null), HttpStatus.BAD_REQUEST);
            }
        }else{
            return new ResponseEntity<>(new ApiResponse(HttpStatus.BAD_REQUEST,"Miss some parameters",null), HttpStatus.BAD_REQUEST);
        }
    }
    
    //取消訂單
    @RequestMapping(value = "/order", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<Object> cancelOrder(HttpServletRequest request)
            throws Exception{
        try{
            Map<String,String> paramMap = getParam(request);

            String account = paramMap.get("account");
            String pwd = paramMap.get("password");
            String token = paramMap.get("token");
            if(StringUtils.isNotBlank(account) && StringUtils.isNotBlank(pwd) && StringUtils.isNotBlank(token)){
                UsersModel userModel = new UsersModel();
                userModel.setAccount(account);
                userModel.setPassword(pwd);
                //確認使用者存在
                Optional<UsersModel> user = userService.userFind(Optional.of(userModel));
                if(!user.isPresent()){
                    return new ResponseEntity<>(new ApiResponse(HttpStatus.NOT_FOUND,"Username or password is wrong",null), HttpStatus.NOT_FOUND);
                }
                if(!user.get().getOrderList().contains(token)){
                    return new ResponseEntity<>(new ApiResponse(HttpStatus.BAD_REQUEST,"This order token is wrong",null), HttpStatus.BAD_REQUEST);
                }
                userService.userCancelOrder(Optional.of(userModel), token);
            }
            return new ResponseEntity<>(new ApiResponse(HttpStatus.OK,"Success",null), HttpStatus.OK);
        }catch(ArrayIndexOutOfBoundsException e){
            return new ResponseEntity<>(new ApiResponse(HttpStatus.BAD_REQUEST,"Miss some parameters",null), HttpStatus.BAD_REQUEST);
        }
    }
}