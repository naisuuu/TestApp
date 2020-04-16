'use strict'
 const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();
 exports.sendNotification = functions.database.ref('/notifications/{user_id}/{notification_id}').onWrite((change, context) => {
const user_id = context.params.user_id;
const notification_id = context.params.notification_id;
console.log('Notification sent to: ',user_id);

if(!context.data.val()){
  return console.log('A notification has been deleted from the firebase: ',notification_id);
}

const payload = {
  notification: {
    title: "Friend Request",
    body:"You've received a new Friend Request",
    icon: "default"
  }
};

return admin.messaging().sendToDevice(/* Token ID */, payload).then(response =>{
  console.log('This was the notification feature');
});
 });
