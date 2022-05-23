# import RPi.GPIO as GPIO
import picamera
import time
import threading
import dropbox 
from datetime import datetime
from subprocess import call
from time import sleep

def startRecording():   
    camera = picamera.PiCamera()

    now = datetime.now()
    dt_string = now.strftime("%d-%m-%Y-%H-%M-%S")
    camera.annotate_text = dt_string
    recordingName = "video_" + dt_string
    
    camera.resolution = (640, 480)
    camera.start_recording(recordingName + ".h264", quality=21)
    camera.wait_recording(20)
    camera.stop_recording()
    
    t1 = threading.Thread(target=convertToMP4, args=(recordingName,))
    t1.daemon = True
    t1.start()
    
    camera.close()

def removeVideoFile(oldName):
    command = "rm " + oldName
    call([command], shell=True)

def convertToMP4(recordingName):
    oldName = recordingName + ".h264"
    newName = recordingName + ".mp4"
    command = "MP4Box -add " + oldName + " " + newName
    call([command], shell=True)
    removeVideoFile(oldName)
    accessDropBox(newName)

def upload_file(access_token, from_file, to_file):
    db = dropbox.Dropbox(access_token) # db short for dropbox

    try:
        f = open(from_file, 'rb')
        db.files_upload(f.read(), to_file)
        removeVideoFile(from_file)
    except:
        print("Unable to upload to dropbox")

def accessDropBox(fileName):
    f = open("credentials.txt", "r")
    access_token = f.readline()
    f.close()

    from_file = fileName
    to_file = '/motionSensorCamera/' + fileName

    upload_file(access_token, from_file, to_file)

def main():
    sensorPin = 8

    GPIO.setmode(GPIO.BOARD)
    GPIO.setup(sensorPin, GPIO.IN)
    
    #Sleep for 60 seconds on start up for motion sensor to establish background reading
    time.sleep(60) 

    try:
        while True:
            GPIO.wait_for_edge(sensorPin, GPIO.RISING)
            time.sleep(0.3)
            if GPIO.input(sensorPin):
                startRecording()

    except KeyboardInterrupt:
        print("Process cancelled")

    finally:
        GPIO.cleanup()
    
if __name__ == '__main__':
    main()
