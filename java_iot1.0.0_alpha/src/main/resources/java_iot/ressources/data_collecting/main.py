###########################################
##                                       ## 
##          MQTT TOPIC SUBSCRIBER        ##
##                                       ##
## The following code will be written in ##
## english because this is my code and   ##
## I can comment it in whatever language ##
##          you can understand.          ##
##                                       ##
##  Main author : ESTIENNE Alban-Moussa  ##
##  This code reads the config.ini file  ##
##  to know what to connect to, and what ##
##           port to log into.           ##
## Add a list of topic you want to keep  ##
##               track of.               ##
##   Add alert threshold values if you   ##
##      want to log unusual activity.    ##
## Overall,just follow the provided .ini ##
##     and you should be good to go.     ##
##                                       ##
###########################################

from struct import pack
import sys, subprocess
import signal
import json as json
import pprint
import datetime
import time
import configparser
import threading
from xml.dom.pulldom import default_bufsize

# Handles the installation of the necessary packages
# Install the given argument
# Asks permission before installing and run the according command based on the running OS
# package IN : the package to install
def install(package):
    if ("linux" in sys.platform):
        import os
        commandStr = 'python3 -m pip install --upgrade ' + package
        os.system(commandStr)
    else:
        subprocess.check_call([sys.executable, "-m", "pip", "install", package])

# Checks if paho-mqtt package is installed. If it isn't, tries to install it before running the program
try:
    import paho.mqtt.client as mqtt
except ImportError or ModuleNotFoundError:
    print("paho-mqtt does not exist. Would you like to install it? (y/n)")
    c = input()
    if c == "y":
        install("paho-mqtt")
        import paho.mqtt.client as mqtt
    else:
        print("Terminating program due to unmet requirements: 1 (one) or more libraries are missing.")
        print("To install library : ")
        print("Open a console terminal")
        print("Run the command : (path to python OR python3) -m pip install paho-mqtt")
        print("Try to run the program back.")
        sys.exit(1)


# --------------------------------------------------------------------------- #

################
## Parameters ##
################

server: str = ""
port: int = 0
keepalive: int = 0
topic: str = ""
step: int = 1
debugMode: bool = False

print("Run in debug mode? (Y/N)")
c = input()
if (c.lower() == "y"):
    debugMode = True

######################
# JSON Buffer Reader #
######################

input_data = open("java_iot1.0.0_alpha\\src\\main\\resources\\java_iot\\ressources\\data_collecting\\data.json", "r")

def print_debug(string):
    if debugMode is True:
        print(string)

##############################
## Dictionnaries and arrays ##
##############################

RUNNING_OS = sys.platform
print_debug(f"OPERATING SYSTEM IS {RUNNING_OS}")
data = {}
listened_rooms = []
fixed_data = {}
try:
    print_debug("################# INPUT DATA FILE PROVIDED : ################# ")
    fixed_data = json.loads(input_data.read())
    print_debug(fixed_data)
except json.JSONDecodeError as e:
    fixed_data = {}

alert_threshold = {}
logged_dataType = []
debounce = {}
topic_list = {}




def config_loader():

    global server
    global port
    global keepalive
    global topic
    global step
    global alert_threshold
    global topic_list
    global logged_dataType

    config = configparser.ConfigParser()
    config.read("java_iot1.0.0_alpha\\src\\main\\resources\\java_iot\\ressources\\data_collecting\\config.ini")

    server = config.get("Connection Infos", "host")
    port = int(config.get("Connection Infos", "port"))
    keepalive = int(config.get("Connection Infos", "keepalive"))

    topic = config.get("Topics", "topics")

    step = int(config.get("Data treatment", "step"))
    
    topic_list = topic.split(",")
    
    alerts = config.get("Data treatment", "alerts")
    dataTypes = config.get("Data treatment", "data_to_keep")
    rooms_to_listen = config.get("Data treatment", "listened_rooms")

    for alert in alerts.split(","):
        if alert != "":
            info = alert.split(":")
            alert_threshold[info[0].strip()] = int(info[1])

    for data in dataTypes.split(","):
        if alert != "":
            logged_dataType.append(data.strip())

    for room in rooms_to_listen.split(","):
        if room != "":
            listened_rooms.append(room.strip())

    print_debug("LOADING BROKER SCRIPT WITH PARAMETERS : ")
    print_debug(f"Host URL : {server}")
    print_debug(f"Port : {port}")
    print_debug(f"kepAlive : {keepalive}")
    print_debug(f"Topics : {topic_list}")
    print_debug(f"Step : {step}")
    print_debug(f"Alerts set : {alert_threshold}")
    print_debug(f"Logged Datas : {logged_dataType}")
    print_debug(f"Listened Rooms : {listened_rooms}")

config_loader()

# --- Save scheduler --- #


# Saves the data dictionnaries to json format
# This function gets called every `step` minutes, as defined in config.ini
def save_to_file():

    global fixed_data

    with open("java_iot1.0.0_alpha\\src\\main\\resources\\java_iot\\ressources\\data_collecting\\data.json", "w") as savefile:
        json.dump(fixed_data, savefile)

    print_debug("Successfully exported data to file.")


# Define the saving process.
# It first unload the content of the messages received during the `step` minutes interval
# into the global and fixed variables
# Calls the file saver to export the data
# Calls another threading process to schedule another loop.
#
# For compatibility reasons, signal.signal(SIGALRM) will not be used, as it prevents the program from running in Windows OS


def saving_interval():
    
    print_debug("Entering saving process...")

    global data
    global fixed_data

    print_debug(data)
    print_debug("####################################################")

    # Unload temp content into global content
    fixed_data.update(data)
    data.clear()

    print_debug("Saving to file...")

    save_to_file()
    if "linux" in RUNNING_OS:
        signal.alarm(step * 60)
    else:
        t = threading.Timer(step * 60, saving_interval)
        t.daemon = True
        t.start()

    print_debug("New loop initialized")

def saving_interval_linux(sig_frame):
    saving_interval()


if "linux" in RUNNING_OS:
    signal.signal(signal.SIGALRM, saving_interval_linux)
    signal.alarm(step * 60)
else:
    t = threading.Timer(step * 60, saving_interval)
    t.daemon = True
    t.start()



# The callback for when the client receives a CONNACK response from the server.
def on_connect(client, userdata, flags, reason_code, properties):

    global topic
    global topic_list

    print_debug(f"Connected with result code {reason_code}")
    print_debug(f"List of topics to subscribe to : {topic}")    
    
    # Subscribing in on_connect() means that if we lose the connection and
    # reconnect then subscriptions will be renewed.
    for t in topic_list:
        print_debug(f"Subscribing to topic {t}")
        client.subscribe(t)



def on_subscribe(client, userdata, mid, reason_code_list, properties):
    if reason_code_list[0].is_failure:
        print_debug(f"Broker rejected you subscription: {reason_code_list[0]}")
    else:
        print_debug(f"Broker granted the following QoS: {reason_code_list[0].value}")

# The callback for when a PUBLISH message is received from the server.
def on_message(client, userdata, msg):
    
    file = json.loads(msg.payload)

    if "AM107/by-room" in msg.topic:

        # Check "Triphaso" for explanation. Same as this.

        room_name = file[1]["room"]
        device_name = file[1]["deviceName"]

        for key in file[0].keys():
            alert = False
            
            if logged_dataType == [] or key in logged_dataType:
                if listened_rooms == [] or file[1]["room"] in listened_rooms:
                    if room_name not in data:
                        data[room_name] = {}

                    if device_name not in data[room_name]:
                        data[room_name][device_name] = {}
                    if key in alert_threshold:
                        if int(file[0][key]) > alert_threshold[key]:
                            alert = True
                    data[file[1]["room"]][file[1]["deviceName"]][key] = [file[0][key], alert]
                    data[file[1]["room"]][file[1]["deviceName"]]["time"] = str(datetime.datetime.now())
        

    elif "Triphaso" in msg.topic:

        # Retrieves the room_name and device_name

        room_name = file[1]["Room"]
        device_name = file[1]["deviceName"]

        # For every data the PUBLISH has received      

        for key in file[0].keys():
            if logged_dataType == [] or key in logged_dataType:
                if listened_rooms == [] or file[1]["Room"] in listened_rooms:
                    # Initializes keys in dictionary if the data isn't already present

                    if room_name not in data:
                        data[room_name] = {}

                    if device_name not in data[room_name]:
                        data[room_name][device_name] = {}  

                    # Launches the alert threshold checker
                    alert = False
                    if key in alert_threshold:
                        if int(file[0][key]) > alert_threshold[key]:
                            alert = True
                    #Input the data in the dictionary
                    this_data = [file[0][key], alert]
                    data[file[1]["Room"]][file[1]["deviceName"]][key] = this_data
                    # After everything, log the date and hour
                    data[file[1]["Room"]][file[1]["deviceName"]]["time"] = str(datetime.datetime.now())

    elif "solaredge" in msg.topic:
        if "Global" not in data:
            data["Global"] = {}
        
        if listened_rooms == [] or "Global" in listened_rooms:
            for dataType in file:
                if dataType in logged_dataType:
                    data["Global"][dataType] = file[dataType]
        

    pprint.pprint(data)



try:
    mqttc = mqtt.Client(mqtt.CallbackAPIVersion.VERSION2)
    mqttc.on_connect = on_connect
    mqttc.on_message = on_message
    mqttc.on_subscribe = on_subscribe

    mqttc.connect(server, int(port), int(keepalive))

    # Blocking call that processes network traffic, dispatches callbacks and
    # handles reconnecting.
    # Other loop*() functions are available that give a threaded interface and a
    # manual interface.
    mqttc.loop_forever()
except KeyboardInterrupt:
    if "linux" not in RUNNING_OS:
        t.cancel()
