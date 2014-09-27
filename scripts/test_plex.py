import serial

ser = serial.Serial('/dev/ttyACM1', 115200)

ser.write('\x00\x0c\xff\xff')

ser.close()