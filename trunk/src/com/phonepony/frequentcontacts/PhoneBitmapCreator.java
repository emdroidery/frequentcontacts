/*
 * Copyright (C) 2010 The Phone Pony Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.phonepony.frequentcontacts;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.ContactsContract;

import java.io.InputStream;
import java.util.Random;

/**
 *
 */
public class PhoneBitmapCreator {
    private Context context;
    // The size of a home screen shortcut icon.
    private int mIconSize;

    public PhoneBitmapCreator(Context context) {
        this.context = context;
        mIconSize = getResources().getDimensionPixelSize(android.R.dimen.app_icon_size);

    }

    private Resources getResources() {
        return context.getResources();
    }

    /**
     * @return randomly generated photo bitmap
     */
    public Bitmap generateRandomPhoneIcon() {

        Bitmap photo = null;
        final int[] fallbacks = {
                R.drawable.ic_contact_picture,
                R.drawable.ic_contact_picture_2,
                R.drawable.ic_contact_picture_3
        };
        photo = BitmapFactory.decodeResource(getResources(), fallbacks[new Random().nextInt(fallbacks.length)]);
        return photo;
    }

    /**
     * Generates a phone number shortcut icon. Adds an overlay describing the type of the phone
     * number, and if there is a photo also adds the call action icon.
     *
     * @param contactUri  The person the phone number belongs to
     * @param type        The type of the phone number
     * @param actionResId The ID for the action resource
     * @return The bitmap for the icon
     */
    public Bitmap generatePhoneNumberIcon(Uri contactUri, int type, int actionResId) {
        final Resources r = getResources();
        boolean drawPhoneOverlay = true;
        final float scaleDensity = getResources().getDisplayMetrics().scaledDensity;

        //Bitmap photo = loadContactPhoto(contactUri, null);
        Bitmap photo = null;
        InputStream input = null;
        try {
            input = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(), contactUri);
        } catch (Exception e) {
            // sometimes happens
        }
        if (input != null) {
            photo = BitmapFactory.decodeStream(input);
        }

        if (photo == null) {
            photo = generateRandomPhoneIcon();
        }

        // Setup the drawing classes
        Bitmap icon = createShortcutBitmap();
        Canvas canvas = new Canvas(icon);

        // Copy in the photo
        Paint photoPaint = new Paint();
        photoPaint.setDither(true);
        photoPaint.setFilterBitmap(true);
        Rect src = new Rect(0, 0, photo.getWidth(), photo.getHeight());
        Rect dst = new Rect(0, 0, mIconSize, mIconSize);
        canvas.drawBitmap(photo, src, dst, photoPaint);

        // Create an overlay for the phone number type
        String overlay = null;
        switch (type)

        {
            case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                overlay = getString(R.string.type_short_home);
                break;

            case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                overlay = getString(R.string.type_short_mobile);
                break;

            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                overlay = getString(R.string.type_short_work);
                break;

            case ContactsContract.CommonDataKinds.Phone.TYPE_PAGER:
                overlay = getString(R.string.type_short_pager);
                break;

            case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER:
                overlay = getString(R.string.type_short_other);
                break;
        }

        if (overlay != null)

        {
            Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DEV_KERN_TEXT_FLAG);
            textPaint.setTextSize(20.0f * scaleDensity);
            textPaint.setTypeface(Typeface.DEFAULT_BOLD);
            textPaint.setColor(r.getColor(R.color.textColorIconOverlay));
            textPaint.setShadowLayer(3f, 1, 1, r.getColor(R.color.textColorIconOverlayShadow));
            canvas.drawText(overlay, 2 * scaleDensity, 16 * scaleDensity, textPaint);
        }

        // Draw the phone action icon as an overlay
        if (drawPhoneOverlay)

        {
            Bitmap phoneIcon = getPhoneActionIcon(r, actionResId);
            if (phoneIcon != null) {
                src.set(0, 0, phoneIcon.getWidth(), phoneIcon.getHeight());
                int iconWidth = icon.getWidth();
                dst.set(iconWidth - ((int) (20 * scaleDensity)), -1,
                        iconWidth, ((int) (19 * scaleDensity)));
                canvas.drawBitmap(phoneIcon, src, dst, photoPaint);
            }
        }

        return icon;
    }

    private String getString(int resId) {
        return context.getString(resId);
    }

    private Bitmap createShortcutBitmap() {
        return Bitmap.createBitmap(mIconSize, mIconSize, Bitmap.Config.ARGB_8888);
    }

//    private Bitmap loadContactPhoto(Uri selectedUri, BitmapFactory.Options options) {
//        Bitmap bm = null;
//        if (selectedUri != null) {
//            Uri contactUri = null;
//            if (ContactsContract.Contacts.CONTENT_ITEM_TYPE.equals(getContentResolver().getType(selectedUri))) {
//                // TODO we should have a "photo" directory under the lookup URI itself
//                contactUri = ContactsContract.Contacts.lookupContact(getContentResolver(), selectedUri);
//            } else {
//
//                Cursor cursor = getContentResolver().query(selectedUri,
//                        new String[]{ContactsContract.Data.CONTACT_ID}, null, null, null);
//                try {
//                    if (cursor != null && cursor.moveToFirst()) {
//                        final long contactId = cursor.getLong(0);
//                        contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
//                    }
//                } finally {
//                    if (cursor != null) cursor.close();
//                }
//            }
//
//            Cursor cursor = null;
//
//            try {
//                Uri photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
//                cursor = getContentResolver().query(photoUri, new String[]{ContactsContract.CommonDataKinds.Photo.PHOTO},
//                        null, null, null);
//                if (cursor != null && cursor.moveToFirst()) {
//                    bm = loadContactPhoto(cursor, 0, options);
//                }
//            } finally {
//                if (cursor != null) {
//                    cursor.close();
//                }
//            }
//
//        }
//        if (bm == null) {
//            final int[] fallbacks = {
//                    R.drawable.ic_contact_picture,
//                    R.drawable.ic_contact_picture_2,
//                    R.drawable.ic_contact_picture_3
//            };
//            bm = BitmapFactory.decodeResource(getResources(),
//                    fallbacks[new Random().nextInt(fallbacks.length)]);
//        }
//
//        return bm;
//    }

    /**
     * Opens an InputStream for the person's photo and returns the photo as a Bitmap.
     * If the person's photo isn't present returns null.
     *
     * @param cursor            the Cursor pointing to the data record containing the photo.
     * @param bitmapColumnIndex the column index where the photo Uri is stored.
     * @param options           the decoding options, can be set to null
     * @return the photo Bitmap
     */
    public static Bitmap loadContactPhoto(Cursor cursor, int bitmapColumnIndex,
                                          BitmapFactory.Options options) {
        if (cursor == null) {
            return null;
        }

        byte[] data = cursor.getBlob(bitmapColumnIndex);
        return BitmapFactory.decodeByteArray(data, 0, data.length, options);
    }

    private ContentResolver getContentResolver() {
        return context.getContentResolver();
    }

    /**
     * Returns the icon for the phone call action.
     *
     * @param r     The resources to load the icon from
     * @param resId The resource ID to load
     * @return the icon for the phone call action
     */
    private Bitmap getPhoneActionIcon(Resources r, int resId) {
        Drawable phoneIcon = r.getDrawable(resId);
        if (phoneIcon instanceof BitmapDrawable) {
            BitmapDrawable bd = (BitmapDrawable) phoneIcon;
            return bd.getBitmap();
        } else {
            return null;
        }
    }


}
