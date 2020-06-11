import telebot
import firebase_admin
from firebase_admin import credentials, firestore, storage
from firebase import firebase



API_TOKEN = "1193228398:AAE00bV1u4UPc-8COJTE6aczeARrecAbU_8"

Firebase_key = {
  "type": "service_account",
  "project_id": "nustown",
  "private_key_id": "d6e881366af78a41907c016229c8bb48c9e0a6d0",
  "private_key": "-----BEGIN PRIVATE KEY-----\nMIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCPhGVdOJ4ZXn44\nbOa1CbKE9Ts82IXcsHs7N/IF4VkjhF31rRDu5l8XXMeINI/eSxyRRjxP1nzdIDWs\nqla7Rv2dhFn+cYtP3JRL6z8W7nUdBSG2L0e0igatn/KP6aJZ8AHPPx9DOArz+yO7\naVkLk0iR8ivrGzCT5E6vr+Cb7M+QogB3Ld2tzxK1VE8Ouba5m0zkW3X9ueRgXOPd\n1dnjcvet0aDVIWnwZxYuy3GYaTgNFbLjk3u25nJzltbZlvihxudQMCUm6LbaLY3P\n96Vowrm00GrhLb5MPDTqbGIya5ZQkYGnrTiT7H15yomjGK73c3+1ZjdyJFp1i/nb\nGXMR80kdAgMBAAECggEAOx2K7zoqDUMZ9L4OzdV/vjqROv/pK71aFUt3Gqr3H6Uf\nWtn91pn341SpL9aOyD7iNojawPwVlLP2x/emICAeyWpB84frnWq7fwYYh6aTgO+j\nMjsJazlLhOsUa+kNocQ1ypsykmqcQGFbF5BOSHFTfCJnqsEZYmGxc7abaz7BRM6I\nOFnZyMSPyazvPMObmRqEXAp9WLjDVE9h5gzyYk4A8i+s+QzDoNC7NrazQk4vUy9V\n/US8s8Yvs0rp9dOiLqAHCAiR/EE/Sn3F1ro5TSYCdh2kNaZI4Qc4BjiIaEvuyrfu\nPXqq9mK7RKpDeQDv4hHPoY+eIZaMLs8W6q+77EhnlQKBgQDDcYGzQjT+eiRGT9c0\nADX1i4HW0+3xIChG5N8Jr4y5KBExQgPa8/PN47/xzZILgKqjicL7ObpF+wVGfxNh\nXrGmHnSSjkcXQYoXybn42kzaximdlwr92nR+JOaXy/ednXHI1DJbN9rX824FfFms\nSMSTieGCxPgp4q6YPAX/cX5jKwKBgQC7/B9Kei8OMClIba5qcLv06k4vnpJ9o5VG\nGhmaaVMDu+xtVGovp7bUyuYUHp7na/NohoViraR2zjcjiS9VDDEqxnsnH9TnUgkD\njpSxJJFRqzJCYscqxutyQEJGBA09HXZLewCQjeza2tVBZ+HyovvfU39SfCz/fC2j\n/Lnsk2oA1wKBgAc3hcaMx1AuSnCeOiAdceCjSiQYp0ykF1im0uWUqemcZSYgfevz\n3ijYJBpVsPLjyWlIiP6l3w7+ee9ked66lGzB6yQOROnC7cp7FHbv+K6LarX7Adjl\n3C9cRXeNfJNBYRY9X66mnVokDwf804LdHQUlrkIZ6+AAEtpkPQR1uGkJAoGAHqbH\ne19c2NVlhdfMfrKPEyo5QW57uFy59EpBLnpJ/Ovhaxo+jjKsjxneVh2AD3/8mmiE\ny0hA7tZFC1hNDXMFeVIPtptsHaO0sI3JFPE/eK+PbF+q1gDFV0W/CKeUeQiax0fx\nGaDqo+zWpOmJYATPSOTWd96jf60BzCFjN3355NUCgYAg9MmGdUzUXbhq4BFzFgaS\nIEddDDBxD7VRX+Q1TVtduyfbd5pejMpZGnv2Xm0qfXulwhsiMw5acMykgdFFyN48\nZ84flaNbrGXmfNjch0WMzrVgVaVD4zUx4wtVyCbxK8JaKbk8lWcXdEnyhboaRP7f\nkEThnlujfjvzAPUH5LbeiQ==\n-----END PRIVATE KEY-----\n",
  "client_email": "firebase-adminsdk-zjbls@nustown.iam.gserviceaccount.com",
  "client_id": "103567080564946862427",
  "auth_uri": "https://accounts.google.com/o/oauth2/auth",
  "token_uri": "https://oauth2.googleapis.com/token",
  "auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
  "client_x509_cert_url": "https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-zjbls%40nustown.iam.gserviceaccount.com"
}

cred = credentials.Certificate(Firebase_key)
default_app = firebase_admin.initialize_app(cred)
db = firestore.client()
fb_app = firebase.FirebaseApplication('https://nustown.firebaseio.com', None)
bot = telebot.TeleBot(API_TOKEN)
print("running")

def start_polling(request): #request paramter added to prevent error in google cloud function
    #request_json = request.get_json()
    #not needed when this code is run manually on local machine
    @bot.message_handler(commands=['start', 'help'])
    def send_welcome(message):
	    bot.reply_to(message, "The bot is starting.")

    @bot.message_handler(func=lambda m: True)
    def echo_all(message):
        bot.reply_to(message," Message Received")
        header = message.text[0:5]
        doc_ref = db.collection('sampleData').document(header)
        doc_ref.set({
        header : message.text
    })

    @bot.message_handler(content_types=['photo'])
    def photo(message):
        fileID = message.photo[-1].file_id
        file_info = bot.get_file(fileID)
        caption = message.caption
        filePath = file_info.file_path
        url = "https://api.telegram.org/file/bot1193228398:AAE00bV1u4UPc-8COJTE6aczeARrecAbU_8/" + str(filePath)
        header = "this is a url"
        doc_ref = db.collection('URLS').document(header)
        doc_ref.set({
        header : url
    })
    bot.polling()
    ending_message = "Function called."
    return ending_message

dummy_argument = "nothing"
start_polling(dummy_argument) #Manually calling the function when being tested on local machine



