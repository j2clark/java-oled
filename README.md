# SSD_1306 OLED on Raspberri pi 3, using Java Spring Boot application

Install Raspian 
https://www.raspberrypi.org/downloads/raspbian/
Choose Lite version

Enable SSH
https://www.raspberrypi.org/documentation/remote-access/ssh/

Enable I2C
https://www.abelectronics.co.uk/kb/article/1/i2c--smbus-and-raspbian-linux

Install Java 8
sudo apt-get remove openjdk*
sudo apt-get install openjdk-8-jre

Create service and set to auto-start
chmod +x /opt/spring-boot/oled.jar
sudo ln -s /opt/spring-boot/oled.jar /etc/init.d/oled
update-rc.d oled defaults


Getting errors in the app log, something about wiringIp not able to find right hardware

"Unable to determine hardware version. I see: Hardware   : BCM2835
,
 - expecting BCM2708 or BCM2709.
If this is a genuine Raspberry Pi then please report this
to projects@drogon.net. If this is not a Raspberry Pi then you
are on your own as wiringPi is designed to support the
Raspberry Pi ONLY."

https://github.com/WiringPi/WiringPi-Node/issues/70
Found issue online which led me to:
sudo apt-get install npm
npm install mfrc522-rpi
