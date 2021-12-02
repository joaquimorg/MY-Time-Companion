/*  Copyright (C) 2015-2020 Andreas Shimokawa, AnthonyDiGirolamo, Carsten
    Pfeiffer, Daniele Gobbetti, Frank Slezak, Julien Pivotto, Kaz Wolfe, Kevin
    Richter, Lukas Veneziano

    This file is part of Gadgetbridge.

    Gadgetbridge is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Gadgetbridge is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>. */
package org.joaquim.my_timecompanion;

public enum NotificationType {

    // TODO: this this pebbleism needs to be moved somewhere else
    UNKNOWN(NotificationsID.NOTIFICATION_GENERIC, NotificationColor.DarkCandyAppleRed),

    AMAZON(NotificationsID.NOTIFICATION_AMAZON, NotificationColor.ChromeYellow),
    BBM(NotificationsID.NOTIFICATION_BLACKBERRY_MESSENGER, NotificationColor.DarkGray),
    CONVERSATIONS(NotificationsID.NOTIFICATION_HIPCHAT, NotificationColor.Inchworm),
    FACEBOOK(NotificationsID.NOTIFICATION_FACEBOOK, NotificationColor.CobaltBlue),
    FACEBOOK_MESSENGER(NotificationsID.NOTIFICATION_FACEBOOK_MESSENGER, NotificationColor.BlueMoon),
    GENERIC_ALARM_CLOCK(NotificationsID.ALARM_CLOCK, NotificationColor.Red),
    GENERIC_CALENDAR(NotificationsID.TIMELINE_CALENDAR, NotificationColor.BlueMoon),
    GENERIC_EMAIL(NotificationsID.GENERIC_EMAIL, NotificationColor.Orange),
    GENERIC_NAVIGATION(NotificationsID.LOCATION, NotificationColor.Orange),
    GENERIC_PHONE(NotificationsID.DURING_PHONE_CALL, NotificationColor.JaegerGreen),
    GENERIC_SMS(NotificationsID.GENERIC_SMS, NotificationColor.VividViolet),
    GMAIL(NotificationsID.NOTIFICATION_GMAIL, NotificationColor.Red),
    GOOGLE_HANGOUTS(NotificationsID.NOTIFICATION_GOOGLE_HANGOUTS, NotificationColor.JaegerGreen),
    GOOGLE_INBOX(NotificationsID.NOTIFICATION_GOOGLE_INBOX, NotificationColor.BlueMoon),
    GOOGLE_MAPS(NotificationsID.NOTIFICATION_GOOGLE_MAPS, NotificationColor.BlueMoon),
    GOOGLE_MESSENGER(NotificationsID.NOTIFICATION_GOOGLE_MESSENGER, NotificationColor.VividCerulean),
    GOOGLE_PHOTOS(NotificationsID.NOTIFICATION_GOOGLE_PHOTOS, NotificationColor.BlueMoon),
    HIPCHAT(NotificationsID.NOTIFICATION_HIPCHAT, NotificationColor.CobaltBlue),
    INSTAGRAM(NotificationsID.NOTIFICATION_INSTAGRAM, NotificationColor.CobaltBlue),
    KAKAO_TALK(NotificationsID.NOTIFICATION_KAKAOTALK, NotificationColor.Yellow),
    KIK(NotificationsID.NOTIFICATION_KIK, NotificationColor.IslamicGreen),
    LIGHTHOUSE(NotificationsID.NOTIFICATION_LIGHTHOUSE, NotificationColor.PictonBlue), // ??? - No idea what this is, but it works.
    LINE(NotificationsID.NOTIFICATION_LINE, NotificationColor.IslamicGreen),
    LINKEDIN(NotificationsID.NOTIFICATION_LINKEDIN, NotificationColor.CobaltBlue),
    MAILBOX(NotificationsID.NOTIFICATION_MAILBOX, NotificationColor.VividCerulean),
    OUTLOOK(NotificationsID.NOTIFICATION_OUTLOOK, NotificationColor.BlueMoon),
    BUSINESS_CALENDAR(NotificationsID.TIMELINE_CALENDAR, NotificationColor.BlueMoon),
    RIOT(NotificationsID.NOTIFICATION_HIPCHAT, NotificationColor.LavenderIndigo),
    SIGNAL(NotificationsID.NOTIFICATION_HIPCHAT, NotificationColor.BlueMoon),
    WIRE(NotificationsID.NOTIFICATION_HIPCHAT, NotificationColor.BlueMoon),
    SKYPE(NotificationsID.NOTIFICATION_SKYPE, NotificationColor.VividCerulean),
    SLACK(NotificationsID.NOTIFICATION_SLACK, NotificationColor.Folly),
    SNAPCHAT(NotificationsID.NOTIFICATION_SNAPCHAT, NotificationColor.Icterine),
    TELEGRAM(NotificationsID.NOTIFICATION_TELEGRAM, NotificationColor.VividCerulean),
    THREEMA(NotificationsID.NOTIFICATION_HIPCHAT, NotificationColor.JaegerGreen),
    KONTALK(NotificationsID.NOTIFICATION_HIPCHAT, NotificationColor.JaegerGreen),
    ANTOX(NotificationsID.NOTIFICATION_HIPCHAT, NotificationColor.JaegerGreen),
    TRANSIT(NotificationsID.LOCATION, NotificationColor.JaegerGreen),
    TWITTER(NotificationsID.NOTIFICATION_TWITTER, NotificationColor.BlueMoon),
    VIBER(NotificationsID.NOTIFICATION_VIBER, NotificationColor.VividViolet),
    WECHAT(NotificationsID.NOTIFICATION_WECHAT, NotificationColor.KellyGreen),
    WHATSAPP(NotificationsID.NOTIFICATION_WHATSAPP, NotificationColor.IslamicGreen),
    YAHOO_MAIL(NotificationsID.NOTIFICATION_YAHOO_MAIL, NotificationColor.Indigo);

    // Note: if you add any more constants, update all clients as well

    public final int icon;
    public final byte color;

    NotificationType(int icon, byte color) {
        this.icon = icon;
        this.color = color;
    }

    /**
     * Returns the enum constant as a fixed String value, e.g. to be used
     * as preference key. In case the keys are ever changed, this method
     * may be used to bring backward compatibility.
     */
    public String getFixedValue() {
        return name().toLowerCase();
    }

    public String getGenericType() {
        switch (this) {
            case GENERIC_EMAIL:
            case GENERIC_NAVIGATION:
            case GENERIC_SMS:
            case GENERIC_ALARM_CLOCK:
                return getFixedValue();
            case FACEBOOK:
            case TWITTER:
            case SNAPCHAT:
            case INSTAGRAM:
            case LINKEDIN:
                return "generic_social";
            case CONVERSATIONS:
            case FACEBOOK_MESSENGER:
            case RIOT:
            case SIGNAL:
            case WIRE:
            case TELEGRAM:
            case THREEMA:
            case KONTALK:
            case ANTOX:
            case WHATSAPP:
            case GOOGLE_MESSENGER:
            case GOOGLE_HANGOUTS:
            case HIPCHAT:
            case SKYPE:
            case WECHAT:
            case KIK:
            case KAKAO_TALK:
            case SLACK:
            case LINE:
            case VIBER:
                return "generic_chat";
            case GMAIL:
            case GOOGLE_INBOX:
            case MAILBOX:
            case OUTLOOK:
            case YAHOO_MAIL:
                return "generic_email";
            case UNKNOWN:
            default:
                return "generic";
        }
    }
}
