# springboot-websocket-chat
## [演示地址](http://111.231.86.225:8888/)
# 前言

* css选择兄弟元素
```
<script>
  function hidetbody(e){
    if($("#"+e.id+" tbody").is(":hidden")){
      $("#"+e.id+" tbody").show();  
      $("#"+e.id+" +div").show(); 
    }else{
      $("#"+e.id+" tbody").hide();  
      $("#"+e.id+" +div").hide(); 
    }
  }
 </script>
 
 <table id="privatechat" class="table" onclick='javascript:hidetbody(this)'>
         <thead>
         <tr>
           <th id="privateuser">私信聊天</th>
         </tr>
         </thead>
         <tbody id="private" style="height:150px; overflow-y:auto; display:block; ">
         <tr class="msg-机器人" hidden><td>00-00 00:00【机器人】对你说：欢迎你来到ocly的聊天室</td></tr>
         </tbody>
       </table>
       <div class="input-group" style="height: 74px">
         <textarea id="contentuser" class="form-control" rows="3" style="resize: none;" placeholder="单击一名在线用户，与其聊天吧...." ></textarea>
         <span class="input-group-btn">
         <button id="sendtouser" class="btn btn-default" type="submit" disabled="disabled" style="height: 100%">发送</button>
         </span>
       </div>
```
