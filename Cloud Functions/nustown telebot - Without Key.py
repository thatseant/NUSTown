import telebot
import firebase_admin
from firebase_admin import credentials, firestore, storage
from firebase import firebase
#Alot of codes are commented out either because I don't need it but I want to keep it or because I only need it for testing on local machine
#all the printing statements are meant for local testing

API_TOKEN = "Telegram Token"

cred = credentials.Certificate(<Firebase Token>)
default_app = firebase_admin.initialize_app(cred)
db = firestore.client()
fb_app = firebase.FirebaseApplication('https://nustown.firebaseio.com', None)
bot = telebot.TeleBot(API_TOKEN)
print("running")
def start_polling(request): #request paramter added to prevent error in google cloud function
    #request_json = request.get_json()
    #not needed when this code is run manually on local machine
    print(request)
    @bot.message_handler(commands=['start', 'help'])
    def send_welcome(message):
	    bot.reply_to(message, "The bot started.")

    @bot.message_handler(content_types=['text'])
    def echo_all(message):
        #bot.reply_to(message, message.text)
        #print(message)
        #temp_dict1 = message
        content_type = message.content_type
        message_id = message.message_id
        sender_name = message.from_user.username
        sender_id = message.from_user.id
        date_sent = message.date
        group_name = message.chat.title
        chat_group_id = message.chat.id
        message_content = message.text
      #  print("Following are the message details")
      #  print("1. Content type: " + content_type )
      #  print("2. Message id: " + str(message_id))
      #  print("3. Name of the sender: " + sender_name)
      #  print("4. Sender ID: " + str(sender_id))
      #  print("5. Date: " + str(date_sent))
      #  print("6. Time: To be done")
      #  print("7. Group Title: " + group_name)
      #  print("8. Chat/Group ID: " + str(chat_group_id))
      #  print("9. Message Content: " + message_content)
      #  print("=================================================================")
        if message_id%1 == 0: #temporily changed to take in every messages
          doc_ref = db.collection(group_name).document(str(message_id))
          doc_ref.set({
          "Content type" : content_type,
          "Message id" : message_id,
          "Name of the sender" : sender_name,
          "Sender ID" : sender_id,
          "Date" : date_sent,
          "Group Title" : group_name,
          "Chat/Group ID" : chat_group_id,
          "message content" : message_content
        })

    @bot.message_handler(content_types=['photo'])
    def photo(message):
        #print(message)
        content_type = message.content_type
        message_id = message.message_id
        sender_name = message.from_user.username
        sender_id = message.from_user.id
        date_sent = message.date
        group_name = message.chat.title
        chat_group_id = message.chat.id
        caption = message.caption
        file_ID = message.photo[-1].file_id
        file_info = bot.get_file(file_ID)
        filePath = file_info.file_path
        photos_url = "https://api.telegram.org/file/bot1193228398:AAEpK5OONCxpEA8Ir6En3X5agiZlTedUJ7A/" + str(filePath)
      #  print("Following are the message details")
      #  print("1. Content type: " + content_type )
      #  print("2. Message id: " + str(message_id))
      #  print("3. Name of the sender: " + sender_name)
      #  print("4. Sender ID: " + str(sender_id))
      #  print("5. Date: " + str(date_sent))
      #  print("6. Time: To be done")
      #  print("7. Group Title: " + group_name)
      #  print("8. Chat/Group ID: " + str(chat_group_id))
      #  print("9. Caption: " + str(caption)) #still have the str conversion function in case there is no caption in the field
      #  print("10. Photo URL: " + photos_url)
    #    fileID = message.photo[-1].file_id
    #    file_info = bot.get_file(fileID)
    #    caption = message.caption
    #    filePath = file_info.file_path
    #    url = "https://api.telegram.org/file/bot<bot token/" + str(filePath)
    #    header = "this is a url"
        doc_ref = db.collection(group_name).document(str(message_id) + " with photo")
        doc_ref.set({
          "Content type" : content_type,
          "Message id" : message_id,
          "Name of the sender" : sender_name,
          "Sender ID" : sender_id,
          "Date" : date_sent,
          "Group Title" : group_name,
          "Chat/Group ID" : chat_group_id,
          "Photo Caption" : str(caption),
          "Photo URL" : photos_url    
    })


    bot.polling()

    ending_message = "Function called."
    return ending_message
    

dummy_argument = "nothing"
start_polling(dummy_argument) #Manually calling the function when being tested on local machinee



