// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');

// The Firebase Admin SDK to access Cloud Firestore.
const admin = require('firebase-admin');
const path = require('path');
const os = require('os');
const fs = require("fs");
var allSettled = require('promise.allsettled');


const serviceAccount = require("./NUSTown-ffc8c62cae11");

const { v4: uuidv4 } = require('uuid');
admin.initializeApp({
    credential: admin.credential.cert(serviceAccount),
    storageBucket: "nustown.appspot.com"
});

const axios = require('axios').default;
const bucket = admin.storage().bucket();
const moment = require('moment');

const { userRecordConstructor } = require('firebase-functions/lib/providers/auth');

var chrono = require('chrono-node');

exports.isPastEvent = functions.https.onRequest(async (req, res) => {

    try {
        promises = []
        await admin.firestore().collection("events").get().then(async (querySnapshot) => {

            for (const doc of querySnapshot.docs) {
                var data = doc.data()
                if (data.time.toDate()  < new Date()) {
                    promises.push(doc.ref.update({
                        "isPastEvent": true
                    }))
                } else {
                    promises.push(doc.ref.update({
                        "isPastEvent": false
                    }))
                }
            }

            await Promise.all(promises)

        return null;

    })
        res.status(200).json({ result: `Success` });
    } catch (err) {
        console.log(err);
        res.status(500).send(err);
    }

});


exports.createEvent = functions.https.onRequest(async (req, res) => {

    try {
        let numberOfPosts = "15"
        let promises = []
        promises.push(createInstaEvents("NUS Kayaking", "3585406095", numberOfPosts))
        promises.push(createInstaEvents("NUS Climbing", "2280311232", numberOfPosts))
        promises.push(createInstaEvents("NUS Students' Cultural Activities Club", "1921006487", numberOfPosts))
        promises.push(createInstaEvents("NUS Life Saving","6809880112", numberOfPosts))
        promises.push(createInstaEvents("NUS Boxing","1529240926", numberOfPosts))
        promises.push(createInstaEvents("NUS Volleyball","5896733377", numberOfPosts))
        promises.push(createInstaEvents("NUS Mountaineering","2166697202", numberOfPosts))
        promises.push(createInstaEvents("NUS Rovers","1509888327", numberOfPosts))
        promises.push(createInstaEvents("NUS Powerlifting","7190465670", numberOfPosts))
        promises.push(createInstaEvents("Red Cross Youth - NUS Chapter","8241519518", numberOfPosts))
        promises.push(createInstaEvents("NUS ODAC","623560288", numberOfPosts))

        await Promise.all(promises)
        res.status(200).json({ result: `Success` });
    } catch (err) {
        console.log(err);
        res.status(500).send(err);
    }

});

exports.nusSync = functions.https.onRequest(async (req, res) => {
    try {
        let todayDate = "2020-06-01"
        let apiURL = "https://nus.campuslabs.com/engage/api/discovery/event/search?endsAfter="+ todayDate +"T12%3A17%3A23%2B08%3A00&orderByField=endsOn&orderByDirection=ascending&status=&take=1000&query="
        await createNSync(apiURL, "event")
        res.status(200).json({ result: `Success` });
    } catch (err) {
        console.log(err);
        res.status(500).send(err);
    }
})

exports.nusClubs = functions.https.onRequest(async (req, res) => {
    try {
        let apiURL = "https://nus.campuslabs.com/engage/api/discovery/search/organizations?orderBy%5B0%5D=UpperName%20asc&top=500&filter=&query=&skip=0"
        await createNSync(apiURL, "club")
        res.status(200).json({ result: `Success` });
    } catch (err) {
        console.log(err);
        res.status(500).send(err);
    }
})

exports.allInsta = functions.https.onRequest(async (req, res) => {
    try {
        let apiURL = "https://nus.campuslabs.com/engage/api/discovery/organization?take=500"
        const JSONData = await axios.get(apiURL)
        let numberOfClubs = JSONData.data.items
        let numberOfPosts = "5"
        let promises = []
        for (let i=0; i<numberOfClubs.length; i++) {
            let clubData = JSONData.data.items[i]
            /* eslint-disable no-await-in-loop */
            await(getInstaFromUsername(clubData, numberOfPosts))
            /* eslint-enable no-await-in-loop */
        }
        // await Promise.all(promises)
        res.status(200).json({ result: `Success` });
    } catch (err) {
        console.log(err);
        res.status(500).send(err);
    }
})

async function allInsta() { //Run Locally
    try {
        let apiURL = "https://nus.campuslabs.com/engage/api/discovery/organization?take=500"
        const JSONData = await axios.get(apiURL)
        let numberOfClubs = JSONData.data.items
        let numberOfPosts = "5"
        let promises = []
        for (let i=0; i<numberOfClubs.length; i++) {
            let clubData = JSONData.data.items[i]
            /* eslint-disable no-await-in-loop */
            await(getInstaFromUsername(clubData, numberOfPosts))
            /* eslint-enable no-await-in-loop */
        }
        // await Promise.all(promises)
    } catch (err) {
        console.log(err);
    }
}

async function getInstaFromUsername(clubData, numberOfPosts) {

    let instaUrl = clubData.socialMedia.InstagramUrl
    let orgName = clubData.name
    if (instaUrl) {
        let instaUsernamePre = instaUrl.split(".com/")[1]
        let instaUsername = instaUsernamePre.split("/")[0]


        var options = {
            url: 'https://www.instagram.com/web/search/topsearch/?query=' + instaUsername,
            method: 'GET',
            headers: {
                'x-requested-with' : 'XMLHttpRequest',
                'accept-language'  : 'en-US,en;q=0.8,pt;q=0.6,hi;q=0.4',
                'User-Agent'       : 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36',
                'referer'          : 'https://www.instagram.com/dress_blouse_designer/',
                'Cookie'           : '',
                'Accept'           : '*/*',
                'Connection'       : 'keep-alive',
                'authority'        : 'www.instagram.com'
            }
        };



        let instaJSON = await axios.get('https://www.instagram.com/web/search/topsearch/?query=' + instaUsername)
        if (instaJSON.data.users[0]) {
            instaID = instaJSON.data.users[0].user.pk
            await createInstaEvents(orgName, instaID, numberOfPosts)
        }

    }
}

async function createNSync(URL, type) {
    let promises = []
    const JSONData = await axios.get(URL)
    const numberResults = JSONData.data.value
    for (i=0; i<numberResults.length; i++) {
        if (type==="event") {
            promises.push(NSyncEventToDatabase(numberResults, i))
        }
        else if (type==="club") {
            promises.push(NSyncClubToDatabase(numberResults, i))
        }
    }
    await Promise.all(promises)
}

async function NSyncClubToDatabase(allClubs, i) {
    let club = allClubs[i]
    let imageURL = ""
    let catID = ""
    if (club.CategoryIds[0]) {
        catID = club.CategoryIds[0]
    }
    let catName = ""
    if (club.CategoryNames[0]) {
        catName = club.CategoryNames[0]
    }
    let websiteKey = ""
    if (club.WebsiteKey) {
        websiteKey = club.WebsiteKey
    }
    let shortName = club.ShortName
    if (!shortName) {
        shortName = club.Name.replace(/\s/g, "")
    }
    let name = ""
    if (club.Name) {
        name = club.Name
    }
    let id = ""
    if (club.Id) {
        id = club.Id
    }
    let branchId = ""
    if (club.BranchId) {
        branchId = club.BranchId
    }
    let clubInfoHttp = ""
    let clubInfoText = ""
    if (club.Description) {
        clubInfoHttp = club.Description
        clubInfoText = clubInfoHttp.replace(/(<([^>]+)>)/ig, '', "")
    }
    if (club.ProfilePicture) {
        imageURL = "https://se-infra-imageserver2.azureedge.net/clink/images/"+ club.ProfilePicture
    }
    let newDoc = {
        name: name,
        id: id,
        branchId: branchId,
        catID: catID,
        catName: catName,
        websiteKey: websiteKey,
        info: clubInfoText,
        url: "https://nus.campuslabs.com/engage/organization/"+ club.WebsiteKey,
        imgUrl: imageURL
    }

    await admin.firestore().collection('clubs').doc(name).get().then(
        async (doc) => {
            if (!doc.exists) {
                await admin.firestore().collection('clubs').doc(name).set(newDoc)
            }
            return null;
        }
    )

}

async function NSyncEventToDatabase(allEvents, i) {
    let event = allEvents[i]
    let eventStartDateTime = new Date(event.startsOn)
    let eventStartDate = moment(eventStartDateTime).format("YYMMDD")
    let id = event.organizationName + "_" + eventStartDate //ID consists of CCA and Event Date (without whitespace)
    let imageURL;
    if (event.imagePath) {
        imageURL = "https://se-infra-imageserver2.azureedge.net/clink/images/"+ event.imagePath
    } else {
        imageURL = "https://se-infra-imageserver2.azureedge.net/clink/images/"+ event.organizationProfilePicture

    }
    let description = event.description
    let description_text = description.replace(/(<([^>]+)>)/ig, '', "")
    const JSONAttendees = await axios.get("https://nus.campuslabs.com/engage/api/discovery/event/"+ event.id +"/rsvpstatistics?")
    let newDoc = {
        // org: event.organizationName,
        // syncEventID: event.id,
        // syncOrgID: event.organizationId,
        // image: id + ".png",
        // info: description_text,
        // time: admin.firestore.Timestamp.fromDate(new Date(event.startsOn)),
        // category: "",
        // numberAttending: JSONAttendees.data.yesUserCount,
        // name: event.name,
        // place: event.location,
        // rating: 3,
        // url: "https://nus.campuslabs.com/engage/event/" + event.id,
        // id: id,
        // imgUrl: imageURL

            org: event.organizationName,
            syncEventID: event.id,
            syncOrgID: event.organizationId,
            info: description_text,
            time: admin.firestore.Timestamp.fromDate(new Date(event.startsOn)),
            numberAttending: JSONAttendees.data.yesUserCount,
            name: event.name,
            place: event.location,
            rating: 3,
            url: "https://nus.campuslabs.com/engage/event/" + event.id,
            id: id,
            imgUrl: imageURL
    }

    await admin.firestore().collection('events').doc(id).get().then(
        async (doc) => {
            if (!doc.exists) {
                await admin.firestore().collection('events').doc(id).set(newDoc)
            } else if (doc.exists) {
                await admin.firestore().collection('events').doc(id).set(newDoc, {merge:true})
            }
            return null;
        }
    )
}

async function createInstaEvents (orgName, userID, numberOfPosts) {
    let promises = []
    let allPosts = {}
    let apiURL = "https://www.instagram.com/graphql/query/?query_hash=f2405b236d85e8296cf30347c9f08c2a&variables=%7B%22id%22%3A%22" + userID + "%22%2C%22first%22%3A" + numberOfPosts + "%2C%22after%22%3A%22%22%7D"
    const used = process.memoryUsage().heapUsed / 1024 / 1024;
    console.log(`The script uses approximately ${Math.round(used * 100) / 100} MB`);
    const JSONData = await axios.get(apiURL)
    console.log('Axios Received: ' + userID);
    const used2 = process.memoryUsage().heapUsed / 1024 / 1024;
    console.log(`The script uses approximately ${Math.round(used2 * 100) / 100} MB`);
    const allMedia = JSONData.data.data.user.edge_owner_to_timeline_media //get field in JSON object containing all posts
    for (i=0; i<numberOfPosts; i++) {
        let newPost = InstaToDatabase(orgName, allMedia, i, userID); //Creates document and store image for each post
            if (newPost) {
                if (allPosts[newPost.id]) {
                    var postToUpdate = allPosts[newPost.id]
                    postToUpdate.updates[newPost.postDate] = [newPost.info, newPost.imgUrl] //add imgURL to update
                    if (newPost.lastUpdate > postToUpdate.lastUpdate) {
                        postToUpdate.lastUpdate = newPost.lastUpdate
                    }
                } else {
                    allPosts[newPost.id] = newPost
                }
            }
    }

    Object.keys(allPosts).forEach(function(key) {
        console.log(key)
        promises.push(uploadEventFirestore(key, allPosts[key]))
    });

    await Promise.all(promises)
}

function InstaToDatabase(orgName, allMedia, i, userID) {
    var id, cat, name, place, url;
    var firstDate, firstText;
    if (allMedia.edges[i]) {
        let imageURL = allMedia.edges[i].node.display_resources[2].src //get url for ith image

        if (allMedia.edges[i].node.edge_media_to_caption.edges[0]) {
            let caption = allMedia.edges[i].node.edge_media_to_caption.edges[0].node.text
            let post_date = moment.unix(allMedia.edges[i].node.taken_at_timestamp).toDate()
            let results = chrono.strict.parse(caption, post_date)
            let casualResults = chrono.parse(caption, post_date)

            if (results.length !== 0) {
                for (j = 0; j < results.length; j++) {
                    if (results[j].text !== "9/20") {
                        if (results[j].tags) {
                            if (results[j].tags.ENMonthNameParser) {
                                continue
                            }
                        }
                        if (!firstDate | (firstDate < post_date)) {
                            firstDate = results[j].start.date()
                            firstText = results[j].text
                            if (!results[j].start.knownValues.day) {
                                if (casualResults.length !== 0) {
                                    if ((casualResults[0].start.knownValues.weekday !== null) | casualResults[0].start.knownValues.day) {
                                        firstDate = casualResults[0].start.date()
                                        firstText = casualResults[0].text
                                    }
                                }
                                if (results[1]) {
                                    if (results[1].start.knownValues.day) {
                                        firstDate = results[1].start.date()
                                        firstText = results[1].text
                                    }
                                }
                            }
                        }
                        if (results[j].start.knownValues.hour) {
                            if (!results[j].start.knownValues.day) {
                                firstDate.setHours(results[j].start.knownValues.hour)
                                if (results[j].start.knownValues.minute) {
                                    firstDate.setMinutes(results[j].start.knownValues.minute)
                                }
                            } else {
                                firstDate = results[j].start.date()
                                firstText = results[j].text
                            }
                            break;
                        }
                    }
                }
            } else if (casualResults.length !== 0) {
                for (j = 0; j < casualResults.length; j++) {
                    if (casualResults[j].text !== "9/20" && casualResults[j].text !== "now" && casualResults[j].text !== "Now") {
                        firstDate = casualResults[j].start.date()
                        firstText = casualResults[j].text
                        break;
                    }
                }
            }

            if (firstDate >= post_date) {//Only runs below if post is a valid event
                stringDate = moment(firstDate).format("YYMMDD");
                // if (userID === "3585406095") {
                //     cat = "Sports Events"
                //     name = "NUS Kayaking"
                //     place = "UTown"
                //     url = "https://www.instagram.com/nuskayaking/"
                //     id = "kayak_" + stringDate
                // } else if (userID === "2280311232") {
                //     cat = "NUS CCAs"
                //     name = "NUS Climbing"
                //     place = "USC"
                //     url = "https://www.instagram.com/nus_climbing/"
                //     id = "climb_" + stringDate
                // } else if (userID === "1921006487") {
                //     cat = "Open Classes"
                //     name = "NUS Dance"
                //     place = "NUS-Wide"
                //     url = "https://www.instagram.com/breakinus/"
                //     id = "dance_" + stringDate
                // } else if (userID === "1529240926") {
                //     cat = "NUS CCAs"
                //     name = "NUS Boxing"
                //     place = "NUS-Wide"
                //     url = "https://www.instagram.com/nusboxing/"
                //     id = "boxing_" + stringDate
                // } else if (userID === "5896733377") {
                //     cat = "Sports Events"
                //     name = "NUS Volleyball"
                //     place = "NUS-Wide"
                //     url = "https://www.instagram.com/nus.volleyball/"
                //     id = "volleyball_" + stringDate
                // } else {
                //     name = "An Event"
                //     cat = "Other Events"
                //     url = ""
                //     place = "Some Place"
                //     id = "other_" + stringDate
                // }

                var updates = {}

                let postDateString = moment(post_date).format("DD MMM")
                updates[postDateString] = [caption, imageURL]

                let newDoc = {
                    image: orgName + "_" + stringDate + ".png",
                    info: caption,
                    time: admin.firestore.Timestamp.fromDate(firstDate),
                    category: "Other Events",
                    numberAttending: 0,
                    name: orgName,
                    org: orgName,
                    place: "A Place",
                    rating: 3,
                    imgUrl: imageURL,
                    url: "",
                    id: orgName + "_" + stringDate,
                    lastUpdate: post_date,
                    postDate: postDateString,
                    updates: updates
                }

                return newDoc


            }
        }
    }

}

async function uploadEventFirestore(id, newDoc) {
    await admin.firestore().collection('events2').doc(id).get().then(
        async (doc) => {
            if (!doc.exists) {
                await admin.firestore().collection('events2').doc(id).set(newDoc)
            } else if (doc.exists) {
                if (doc.data().lastUpdate) {
                    if (newDoc.lastUpdate < doc.data().lastUpdate.toDate()) {
                        console.log(doc.data().lastUpdate.toDate())
                        newDoc.lastUpdate = doc.data().lastUpdate.toDate()
                    }
                }
                await admin.firestore().collection('events2').doc(id).update({["updates." + newDoc.postDate]: [newDoc.info, newDoc.imgUrl], "org": newDoc.org, "lastUpdate": newDoc.lastUpdate})
            }
            return null;
        }
)
}


allInsta()


//trigger when a new use signed up
exports.newUserSignUp = functions.auth.user().onCreate(user => {
    // for background triggers you must return a value/promise
    return admin.firestore().collection('users').doc(user.uid).set({
      email: user.email,
      displayname : user.email,
      eventAttending: [],
      jioEventAttending: [],
      clubsSubscribedTo : []
    });
  });

//trigger when a user is deleted
exports.userDeleted = functions.auth.user().onDelete(user => {
    const doc = admin.firestore().collection('users').doc(user.uid);
    return doc.delete();
  });

  //test function
  exports.testFunction = functions.https.onCall((data,context) => {
    return admin.firestore().collection('users').doc("testingforjioevent").set(
      //{user_id : data.email, //this is actually the Uid
      //event_id : data.event_id} 
      data
    )
  }
  )

exports.rsvpFunction = functions.https.onCall(async (data, context) => {

  const userdoc = admin.firestore().collection('users').doc(data.email); 
  const eventdoc = admin.firestore().collection('events').doc(data.event_id);
  const event = await eventdoc.get();
  const user = await userdoc.get();

  //checking if the user is already attending
  if(user.data().eventAttending.includes(data.event_id)){ //if this is true, the user is attending
  await userdoc.update({
    eventAttending: admin.firestore.FieldValue.arrayRemove(data.event_id)
  });

  await eventdoc.update({
    usersAttending: admin.firestore.FieldValue.arrayRemove(data.email)
  });
  
  return eventdoc.update({
    numberAttending: admin.firestore.FieldValue.increment(-1)
  });
  }
  else {
  await userdoc.update({
    eventAttending: admin.firestore.FieldValue.arrayUnion(data.event_id)
  });

  await eventdoc.update({
    usersAttending: admin.firestore.FieldValue.arrayUnion(data.email)
  });

  return eventdoc.update({
    numberAttending: admin.firestore.FieldValue.increment(1)
  });
  }
})

// allInsta();

exports.rsvpJioFunction = functions.https.onCall(async (data, context) => {

    const userdoc = admin.firestore().collection('users').doc(data.email); 
    const eventdoc = admin.firestore().collection('jios').doc(data.event_id);
    const event = await eventdoc.get();
    const user = await userdoc.get();
  
    //checking if the user is already attending
    if(user.data().jioEventAttending.includes(data.event_id)){ //if this is true, the user is attending
    await userdoc.update({
      jioEventAttending: admin.firestore.FieldValue.arrayRemove(data.event_id)
    });
  
    await eventdoc.update({
      usersAttending: admin.firestore.FieldValue.arrayRemove(data.email)
    });
    
    return eventdoc.update({
      numberAttending: admin.firestore.FieldValue.increment(-1)
    });
    }
    else {
    await userdoc.update({
      jioEventAttending: admin.firestore.FieldValue.arrayUnion(data.event_id)
    });
  
    await eventdoc.update({
      usersAttending: admin.firestore.FieldValue.arrayUnion(data.email)
    });
  
    return eventdoc.update({
      numberAttending: admin.firestore.FieldValue.increment(1)
    });
    }
  })

  //clubSubscription
  exports.subscribeToClub = functions.https.onCall(async (data, context) => {
      const userdoc = admin.firestore().collection('users').doc(data.email);
      const clubdoc = admin.firestore().collection('clubs').doc(data.club_name);
      const user = await userdoc.get();
      const club = await clubdoc.get();

      //checking if the user is already part of the club
      if(user.data().clubsSubscribedTo.includes(data.club_name)){
      return userdoc.update({
        clubsSubscribedTo: admin.firestore.FieldValue.arrayRemove(data.club_name)
      });

      //return clubdoc.update({
      //  members: admin.firestore.FieldValue.arrayRemove(data.email)
      //});
      }
      else {
      return userdoc.update({
        clubsSubscribedTo: admin.firestore.FieldValue.arrayUnion(data.club_name)
      });

      //return clubdoc.update({
      //  members: admin.firestore.FieldValue.arrayUnion(data.email)
      //});
   
      }
  })

exports.downloadEventsPhoto = functions.https.onRequest(async (req, res) => {
    try {
        promises = []
        await admin.firestore().collection("events").get().then(async (querySnapshot) => {

            for (const doc of querySnapshot.docs) {
                promises.push(
                    downloadToCloud(doc.data().id, doc.data().imgUrl)
                )
            }

            await allSettled(promises)
            allSettled.shim()


            return null;
        });


        res.status(200).json({result: `Success`});
    } catch (err) {
        console.log(err);
        res.status(500).send(err);
    }
})

async function downloadToCloud(id, imageURL) {
      if (imageURL!== "") {
          const picName = id + ".png";
          const tempFilePath = path.join(os.tmpdir(), picName);
          await download(imageURL, tempFilePath).then(() => bucket.upload(tempFilePath,
              {destination: "events/"+ picName,  metadata: {metadata :{
                          firebaseStorageDownloadTokens: uuidv4(),
                      }
                  },}))
      }

}

async function download (newURL, downloadPath) {
    const url = newURL
    const path = downloadPath
    const writer = fs.createWriteStream(path)

    const response = await axios({
        url,
        method: 'GET',
        responseType: 'stream'
    })

    console.log("success insta")

    response.data.pipe(writer)

    return new Promise((resolve, reject) => {
        writer.on('finish', resolve)
        writer.on('error', reject)
    })
}
