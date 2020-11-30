# 关于 IOS 浏览器不能播放视频的解决方案

HTML5 新标签 <video> 可以实现音/视频的播放功能。现代浏览器都是支持video标签的。

一个简单的视频播放的html代码：

```html
<video autoplay loop>
    <source src="video/xxx.mp4" type="video/mp4">
    <source src="video/xxx.webm" type="video/webm">
</video>
```

在上述代码中，source的源直接填写一个视频资源路径即可，对于PC网页和 android 的 h5 页面都可以播放视频，但是如果是苹果手机或者Mac电脑，此source的源需要填写一个后台请求路径，通过后台获取视频资源。

如此，你以为你已经搞定视频播放功能的话？那么恭喜你入坑了。IOS系列设备并不能如愿的播放视频。

查询资料以及实验发现，safari 浏览器在播放视频时，先是发送一个请求探测文件的大小，以此来确定该视频是否可播放（这个地方也有说法是请求一个字节的数据），该请求是携带 range 请求头的（http协议的响应头 `Accept-Ranges`），它表示请求从xx字节到xx字节的数据，并且响应的状态码必须是200。之后再多次发送请求分段获取数据流的数据，此时响应状态码为206。

除此以外，contentType 得设置为 video/mp4 。

如下是后台封装的视频请求代码，可以直接使用。

```java
private void sendVideo(HttpServletRequest request, HttpServletResponse response, File file, String fileName) throws FileNotFoundException, IOException {
        RandomAccessFile randomFile = new RandomAccessFile(file, "r");//只读模式
        long contentLength = randomFile.length();
        String range = request.getHeader("Range");
        int start = 0, end = 0;
        if(range != null && range.startsWith("bytes=")){
            String[] values = range.split("=")[1].split("-");
            start = Integer.parseInt(values[0]);
            if(values.length > 1){
                end = Integer.parseInt(values[1]);
            }
        }
        int requestSize = 0;
        if(end != 0 && end > start){
            requestSize = end - start + 1;
        } else {
            requestSize = Integer.MAX_VALUE;
        }
  
        response.setContentType("video/mp4");
        response.setHeader("Accept-Ranges", "bytes");
        response.setHeader("ETag", fileName);
        response.setHeader("Last-Modified", new Date().toString());
        //第一次请求只返回content length来让客户端请求多次实际数据
        if(range == null){
            response.setHeader("Content-length", contentLength + "");
        }else{
            //以后的多次以断点续传的方式来返回视频数据
            response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);//206
            long requestStart = 0, requestEnd = 0;
            String[] ranges = range.split("=");
            if(ranges.length > 1){
                String[] rangeDatas = ranges[1].split("-");
                requestStart = Integer.parseInt(rangeDatas[0]);
                if(rangeDatas.length > 1){
                    requestEnd = Integer.parseInt(rangeDatas[1]);
                }
            }
            long length = 0;
            if(requestEnd > 0){
                length = requestEnd - requestStart + 1;
                response.setHeader("Content-length", "" + length);
                response.setHeader("Content-Range", "bytes " + requestStart + "-" + requestEnd + "/" + contentLength);
            }else{
                length = contentLength - requestStart;
                response.setHeader("Content-length", "" + length);
                response.setHeader("Content-Range", "bytes "+ requestStart + "-" + (contentLength - 1) + "/" + contentLength);
            }
        }
        ServletOutputStream out = response.getOutputStream();
        int needSize = requestSize;
        randomFile.seek(start);
        while(needSize > 0){
            byte[] buffer = new byte[4096];
            int len = randomFile.read(buffer);
            if(needSize < buffer.length){
                out.write(buffer, 0, needSize);
            } else {
                out.write(buffer, 0, len);
                if(len < buffer.length){
                    break;
                }
            }
            needSize -= buffer.length;
        }
        randomFile.close();
        out.close();
         
    }
```



关于徽商银行在线客服项目中的使用案例：

前端代码部分

```javascript
if($.os.ios){
    mgs = `
		<video controls x5-playsinline="true" playsinline="true" style="object-			fit:fill;" webkit-laysinline="true" x-webkit-airplay="true" 					height="200" width="200" index="100">
			<source src="/IMClient/event/getSource?path=" type="vedio/mp4" />	
		</video>
	`;
}else{
    mgs = `
		<video controls x5-playsinline="true" playsinline="true" style="object-			fit:fill;" webkit-laysinline="true" x-webkit-airplay="true" 					height="200" width="200" index="100">
			<source src="url" type="vedio/mp4" />
		</video>
	`;
}
```

$.os.ios 是 zepto.js 提供判断苹果和安卓类型的方法。当然我们也可以自己手写一个方法。如下：

```javascript
var os = () => {
    var u = navigator.userAgent;
    //android终端
    var isAndroid = u.indexOf('Android') > -1 || u.indexOf('Adr') > -1;
    //ios终端
    var isIOS = !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/); 
    if(isAndroid){
        return "android";
    }else if(isIOS){
        return "ios";      
    }else{
        return undefined;
    }
}
```

