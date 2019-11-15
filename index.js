import {NativeModules} from 'react-native';

var RNLocalNotifications = {
    createNotification: function (id, text, datetime, sound, hiddendata = '') {
        NativeModules.RNLocalNotifications.createNotification(id, text, datetime, sound, hiddendata);
    },
    deleteNotification: function (id) {
        NativeModules.RNLocalNotifications.deleteNotification(id);
    },
    updateNotification: function (id, text, datetime, sound, hiddendata = '') {
        NativeModules.RNLocalNotifications.updateNotification(id, text, datetime, sound, hiddendata);
    },
    setAndroidIcons: function (largeIconName, largeIconType, smallIconName, smallIconType) {
        NativeModules.RNLocalNotifications.setAndroidIcons(largeIconName, largeIconType, smallIconName, smallIconType);
    },
    createNotificationWithDeepLink: function(
        id,
        timestampMs,
        title,
        body,
        deepLink
    ) {
        NativeModules.RNLocalNotifications.createNotificationWithDeepLink(
            id,
            timestampMs,
            title,
            body,
            deepLink
        );
    }
};

export default RNLocalNotifications;
