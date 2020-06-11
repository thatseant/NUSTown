import telebot
import firebase_admin
from firebase_admin import credentials, firestore, storage
from firebase import firebase







cred = credentials.Certificate(Firebase_key)
default_app = firebase_admin.initialize_app(cred)
db = firestore.client()
fb_app = firebase.FirebaseApplication('https://nustown.firebaseio.com', None)
bot = telebot.TeleBot(<bot tolen>)
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
        url = "https://api.telegram.org/file/bot<bot token/" + str(filePath)
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



