Get SSH working (on reboot)
`sudo systemctl enable ssh`

Install pi4j (http://pi4j.com/install.html)
`curl -s get.pi4j.com | sudo bash`

Possibly follow these Python-centric instructions:
https://www.abelectronics.co.uk/kb/article/1/i2c--smbus-and-raspbian-linux

Install Oracle Java 8 (rediculous performance boost) maybe use 
`sudo apt-add-repository ppa:webupd8team/java`
`sudo apt-get update`
`sudo apt-get install oracle-java8-installer`




Create app directory, create service, set to autostart
(docs from spring Reference http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/html/deployment-install.html#deployment-service)

`sudo mkdir /opt/oled`
# copy oled.jar into /opt/oled
`sudo chmod +x oled.jar`
`sudo ln -s /opt/oled/oled.jar /etc/init.d/oled`
`sudo update-rc.d oled defaults`

update-rc.d is for scripting, maybe use systemctl instead?
`sudo systemctl enable oled`