package pro.salebot.mobileclient.utils

import android.app.*
import android.content.ContentResolver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.Drawable
import android.media.AudioAttributes
import android.net.Uri
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import pro.salebot.mobileclient.Constants
import pro.salebot.mobileclient.R
import pro.salebot.mobileclient.mvp.messages.MessagesActivity

class NotificationUtils {

    companion object {

        const val CHANNEL_ID = "salebot_notification"

        fun show(
            ctx: Context,
            idProject: Int,
            idRoom: Int,
            title: String,
            text: String,
            imageLink: String,
            clientType: Int,
            delivered: Boolean
        ) {
            Glide.with(ctx)
                .asBitmap()
                .load(imageLink)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onLoadCleared(placeholder: Drawable?) {

                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        show(
                            ctx,
                            idProject,
                            idRoom,
                            title,
                            text,
                            imageLink,
                            null,
                            clientType,
                            delivered
                        )
                    }

                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        show(
                            ctx,
                            idProject,
                            idRoom,
                            title,
                            text,
                            imageLink,
                            resource,
                            clientType,
                            delivered
                        )
                    }
                })
        }

        fun show(
            ctx: Context,
            idProject: Int,
            idRoom: Int,
            title: String,
            text: String,
            imageLink: String?,
            bitmap: Bitmap?,
            clientType: Int,
            delivered: Boolean
        ): Notification {

            val notificationManager =
                ctx.getSystemService(NOTIFICATION_SERVICE) as NotificationManager?

            val sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + ctx.packageName + "/" + R.raw.notification)

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                val name = "Уведомления"
                val description = "Уведомления о новых сообщениях"
                val importance = NotificationManager.IMPORTANCE_HIGH
                val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
                mChannel.description = description
                mChannel.enableLights(true)
                mChannel.lightColor = Color.BLUE
                mChannel.enableVibration(true)
                mChannel.vibrationPattern = longArrayOf(100, 100, 100, 100, 100)
                mChannel.setShowBadge(false)
                val attributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build()
                mChannel.setSound(sound, attributes)
                notificationManager?.createNotificationChannel(mChannel)
            }

            val resultIntent = Intent(ctx, MessagesActivity::class.java)

            resultIntent.action = "dummy_action_$idRoom"

            resultIntent.putExtra(Constants.EXTRA_ID_PROJECT, idProject.toString())
            resultIntent.putExtra(Constants.EXTRA_ID_ROOM, idRoom.toString())
            resultIntent.putExtra(Constants.EXTRA_NAME_ROOM, title)
            resultIntent.putExtra(Constants.EXTRA_IMAGE_ROOM, imageLink)
            resultIntent.putExtra(Constants.EXTRA_CLIENT_TYPE, clientType)

            val stackBuilder = TaskStackBuilder.create(ctx)
            stackBuilder.addParentStack(MessagesActivity::class.java)
            stackBuilder.addNextIntent(resultIntent)
            val resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_ONE_SHOT
            )

            val builder = NotificationCompat.Builder(ctx, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setLargeIcon(NotificationUtils().getCircleBitmap(bitmap))
                .setContentTitle(title)
                .setContentText(
                    if (delivered) text else ctx.getString(
                        R.string.message_not_delivered_notif,
                        text
                    )
                )
                .setAutoCancel(true)
                .setSound(sound)

            builder.setContentIntent(resultPendingIntent)

            val notification = builder.build()

            notificationManager?.notify(idRoom, notification)
            return notification
        }

        fun cancel(context: Context, id: Int) {
            val notificationManager =
                context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager?
            notificationManager?.cancel(id)
        }

        fun cancelAll(context: Context) {
            val notificationManager =
                context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager?
            notificationManager?.cancelAll()
        }

    }

    private fun getCircleBitmap(bitmap: Bitmap?): Bitmap? {
        bitmap?.let {
            try {
                val output = Bitmap.createBitmap(
                    bitmap.width,
                    bitmap.height, Bitmap.Config.ARGB_8888
                )
                val canvas = Canvas(output)

                val color = Color.RED
                val paint = Paint()
                val rect = Rect(0, 0, bitmap.width, bitmap.height)
                val rectF = RectF(rect)

                paint.isAntiAlias = true
                canvas.drawARGB(0, 0, 0, 0)
                paint.color = color
                canvas.drawOval(rectF, paint)

                paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
                canvas.drawBitmap(bitmap, rect, rect, paint)

                bitmap.recycle()

                return output
            } catch (e: Exception) {
                return null
            }
        }
        return null
    }

}