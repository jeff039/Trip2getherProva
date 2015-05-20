package group.lis.uab.trip2gether.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import group.lis.uab.trip2gether.R;
import group.lis.uab.trip2gether.Resources.Encrypt;
import group.lis.uab.trip2gether.Resources.Utils;
import group.lis.uab.trip2gether.controller.EditTripForm;
import group.lis.uab.trip2gether.controller.Friends;
import group.lis.uab.trip2gether.controller.NotificationList;

/**
 * Created by Jofré on 27/04/2015.
 */
public class NotificationListAdapter extends ArrayAdapter<Notification> {
    private Context context;
    private int layoutResourceId;
    private User myUser;
    private ArrayList<Notification> notifications = null;

    /**
     * Constructor class NotificationListAdapter
     * @param context
     * @param resource
     * @param notifications
     */
    public NotificationListAdapter(Context context, int resource, ArrayList<Notification> notifications,
                                   User myUser) {
        super(context, resource, notifications);
        this.context = context;
        this.layoutResourceId = resource;
        this.notifications = notifications;
        this.myUser = myUser;
    }

    static class NotificationListHolder {
        TextView txtTitle;
        TextView txtSubtitle;
        ImageView imgConfirm;
        ImageView imgDecline;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        NotificationListHolder holder = null;

        if(row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new NotificationListHolder();

            holder.txtTitle = (TextView)row.findViewById(R.id.txtTitle);
            holder.txtSubtitle = (TextView)row.findViewById(R.id.txtSubtitle);

            holder.imgConfirm = (ImageView)row.findViewById(R.id.imgConfirm);
            holder.imgDecline = (ImageView)row.findViewById(R.id.imgDecline);

            row.setTag(holder);
        }else {
            holder = (NotificationListHolder)row.getTag();
        }

        try {
            List<ParseObject> usuarios = Utils.getRegistersFromBBDD(notifications.get(position).getIdEmisor(),
                    "Usuario", "objectId");
            ParseObject idUsuario = usuarios.get(0);
            holder.txtTitle.setText(idUsuario.getString("Mail"));

        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (notifications.get(position).getTipo().compareTo("Amistad")==0) {
            holder.txtSubtitle.setText(R.string.friendRequest);
            holder.imgConfirm.setImageResource(R.drawable.ic_action_confirm);
            holder.imgDecline.setImageResource(R.drawable.ic_action_decline);
        }

        if (notifications.get(position).getTipo().compareTo("add")==0) {
            holder.txtSubtitle.setText(R.string.notificationAdd);
            holder.imgConfirm.setEnabled(false);
            holder.imgDecline.setEnabled(false);
        }

        if (notifications.get(position).getTipo().compareTo("delete")==0) {
            holder.txtSubtitle.setText(R.string.notificationDelete);
            holder.imgConfirm.setEnabled(false);
            holder.imgDecline.setEnabled(false);
        }

        if (notifications.get(position).getTipo().compareTo("drop")==0) {
            holder.txtSubtitle.setText(R.string.notificationDrop);
            holder.imgConfirm.setEnabled(false);
            holder.imgDecline.setEnabled(false);
        }

        if (notifications.get(position).getTipo().compareTo("accept")==0) {
            holder.txtSubtitle.setText(R.string.notificationAccept);
            holder.imgConfirm.setEnabled(false);
            holder.imgDecline.setEnabled(false);
        }

        if (notifications.get(position).getTipo().compareTo("decline")==0) {
            holder.txtSubtitle.setText(R.string.notificationDecline);
            holder.imgConfirm.setEnabled(false);
            holder.imgDecline.setEnabled(false);
        }

        final NotificationListHolder finalHolder = holder;
        holder.imgConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Boolean friendship = friendship(notifications.get(position).getIdEmisor(), notifications.get(position).getIdReceptor());
                    if (friendship) {
                        Boolean updateNotification = updateNotification(notifications.get(position).getObjectId(),
                                notifications.get(position).getIdReceptor(), notifications.get(position).getIdEmisor(),
                                true);
                        if (updateNotification) {
                            finalHolder.imgConfirm.setVisibility(View.INVISIBLE);
                            finalHolder.imgConfirm.setEnabled(false);
                            finalHolder.imgDecline.setVisibility(View.INVISIBLE);
                            finalHolder.imgDecline.setEnabled(false);
                            finalHolder.txtSubtitle.setText(R.string.friendRequestConfirm);
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        holder.imgDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean updateNotification = null;
                try {
                    updateNotification = updateNotification(notifications.get(position).getObjectId(),
                            notifications.get(position).getIdReceptor(), notifications.get(position).getIdEmisor(),
                            false);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (updateNotification) {
                    finalHolder.imgConfirm.setVisibility(View.INVISIBLE);
                    finalHolder.imgConfirm.setEnabled(false);
                    finalHolder.imgDecline.setVisibility(View.INVISIBLE);
                    finalHolder.imgDecline.setEnabled(false);
                    finalHolder.txtSubtitle.setText(R.string.friendRequestDecline);

                }
            }
        });
        return row;
    }

    public boolean friendship(String idUsuario1, String idUsuario2) throws ParseException {
        boolean success = false;
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("idUsuario1", idUsuario1);
        params.put("idUsuario2", idUsuario2);

        //Creem la nova amistad a la BD (1)
        String friendshipResponse = ParseCloud.callFunction("friendship", params); //crida al BE
        if(friendshipResponse.isEmpty() == false) {
            //Creem la nova amistad a la BD (2)
            params.clear();
            params.put("idUsuario1", idUsuario2);
            params.put("idUsuario2", idUsuario1);
            String friendshipResponse2 = ParseCloud.callFunction("friendship", params);
            if(friendshipResponse2.isEmpty() == false) {
                success = true;
            }
        }
        return success;
    }

    public boolean updateNotification(String objectId, String idReceptor, String idEmisor,
                                      Boolean accept) throws ParseException {
        boolean success = false;
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("objectId", objectId);
        HashMap<String, Object> params2 = new HashMap<String, Object>();
        params2.put("transmitterId", idReceptor);
        params2.put("receiverId", idEmisor);

        ParseObject updateNotificationResponse = ParseCloud.callFunction("updateNotification", params);
        if(updateNotificationResponse.getObjectId() != null) {
            if (accept) {
                params2.put("type", "accept");
            }else {
                params2.put("type", "decline");
            }
            String acceptNotification = ParseCloud.callFunction("addNotification", params2);
            if (!acceptNotification.isEmpty()) {
                success = true;
            }
        }
        return success;
    }
}
