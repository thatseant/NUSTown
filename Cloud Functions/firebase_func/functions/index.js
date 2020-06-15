// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');

// The Firebase Admin SDK to access Cloud Firestore.
const admin = require('firebase-admin');
const path = require('path');
const os = require('os');
const fs = require("fs");
const serviceAccount = require("./NUSTown-ffc8c62cae11");
const { v4: uuidv4 } = require('uuid');
admin.initializeApp({
    credential: admin.credential.cert(serviceAccount),
    storageBucket: "nustown.appspot.com"
});
const axios = require('axios').default;
const bucket = admin.storage().bucket();
const moment = require('moment');
var chrono = require('chrono-node');

exports.createEvent = functions.https.onRequest(async (req, res) => {

    try {
        let numberOfPosts = "15"
        await createProfileEvents("3585406095", numberOfPosts)
        await createProfileEvents("2280311232", numberOfPosts)
        await createProfileEvents("1921006487", numberOfPosts)
        res.status(200).json({ result: `Success` });
    } catch (err) {
        console.log(err);
        res.status(500).send(err);
    }

});

async function createProfileEvents (userID, numberOfPosts) {
    let promises = []
    let apiURL = "https://www.instagram.com/graphql/query/?query_hash=f2405b236d85e8296cf30347c9f08c2a&variables=%7B%22id%22%3A%22" + userID + "%22%2C%22first%22%3A" + numberOfPosts + "%2C%22after%22%3A%22%22%7D"
    const JSONData = await axios.get(apiURL)
    const allMedia = JSONData.data.data.user.edge_owner_to_timeline_media //get field in JSON object containing all posts
    for (i=0; i<numberOfPosts; i++) {
        promises.push(tasks(allMedia, i, userID)); //Creates document and store image for each post
    }

    await Promise.all(promises)
}

async function tasks(allMedia, i, userID) {
    var id, cat, name, place, url;
    var firstDate, firstText;
    let imageURL = allMedia.edges[i].node.display_resources[2].src //get url for ith image
    let caption = allMedia.edges[i].node.edge_media_to_caption.edges[0].node.text
    let date = moment.unix(allMedia.edges[i].node.taken_at_timestamp)
    let results = await chrono.strict.parse(caption, date.toDate())
    let casualResults = await chrono.parse(caption, date.toDate())

    if (results.length!==0) {
        for (j = 0; j < results.length; j++) {
            if (results[j].text !== "9/20") {
                if (results[j].tags) {
                    if (results[j].tags.ENMonthNameParser) {
                        continue
                    }
                }
                firstDate = results[j].start.date()
                firstText = results[j].text
                break;
            }
        }
    }

    if (firstDate >= date.toDate()) {//Only runs below if post is a valid event
        stringDate = moment(firstDate).format("YYMMDD");
        if (userID === "3585406095") {
            cat = "Sports Events"
            name = "Some Kayak Event"
            place = "UTown"
            url = "https://www.instagram.com/nuskayaking/"
            id = "kayak_" + stringDate
        }
        else if (userID === "2280311232") {
            cat = "NUS CCAs"
            name = "Some Climb Event"
            place = "USC"
            url = "https://www.instagram.com/nus_climbing/"
            id = "climb_" + stringDate
        }
        else if (userID === "1921006487") {
            cat = "Open Classes"
            name = "Some Dance Event"
            place = "NUS-Wide"
            url = "https://www.instagram.com/breakinus/"
            id = "dance_" + stringDate
        }
        else {
            cat = "Other Events"
            id = "other_" + stringDate
        }
        let newDoc = {
            image: id + ".png",
            info: caption,
            time: moment(firstDate).format("dddd, MMMM Do YYYY, h:mm:ss a"),
            category: cat,
            numberAttending: 0,
            name: name,
            place: place,
            rating: 3,
            url: url,
            id: id
        }


        await admin.firestore().collection('events').doc(id).get().then(
            async (doc) => {
                if (!doc.exists) {
                    await admin.firestore().collection('events').doc(id).set(newDoc)
                    const picName = id + ".png";
                    const tempFilePath = path.join(os.tmpdir(), picName);
                    await download(imageURL, tempFilePath).then(() => bucket.upload(tempFilePath,
                        {destination: "events/"+ picName,  metadata: {metadata :{
                                    firebaseStorageDownloadTokens: uuidv4(),
                                }
                            },}))
                }
                return null;
            }
        )
    }

}

// await db.runTransaction(t => { //transaction method
//     return t.get(docRef)
//         .then(doc => {
//             if (!doc.exists) {
//                 t.set(docRef, newDoc)
//             } else {
//
//             }
//         });
// }).then(async result => {
//     const picName = id + ".png";
//     const tempFilePath = path.join(os.tmpdir(), picName);
//     await download(imageURL, tempFilePath).then(() => bucket.upload(tempFilePath,
//         {destination: "events/"+ picName,  metadata: {metadata :{
//                     firebaseStorageDownloadTokens: uuidv4(),
//                 }
//             },}))
// }).catch(err => {
//     console.log('Transaction failure:', err);
// });

async function download (newURL, downloadPath) {
    const url = newURL
    const path = downloadPath
    const writer = fs.createWriteStream(path)

    const response = await axios({
        url,
        method: 'GET',
        responseType: 'stream'
    })

    response.data.pipe(writer)

    return new Promise((resolve, reject) => {
        writer.on('finish', resolve)
        writer.on('error', reject)
    })
}
