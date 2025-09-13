FROM openjdk:17-bullseye

MAINTAINER shaohui.weng
VOLUME /tmp
RUN mkdir /root/prop
RUN mkdir /root/jar
#RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.aliyun.com/g' /etc/apk/repositories
##添加字体以支持验证码显示
#RUN apk add --update font-adobe-100dpi ttf-dejavu fontconfig

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime

EXPOSE 8080

#ADD ./src/main/resources/jeecg ./config/jeecg
ADD ./target/jeecg-system-start-3.8.0.jar /root/jar/jar.jar

#CMD sleep 60;java -Djava.security.egd=file:/dev/./urandom -jar jeecg-system-start-3.8.0.jar
CMD sleep 60;java -Djava.security.egd=file:/dev/./urandom -Xbootclasspath/a:/root/prop -jar /root/jar/jar.jar