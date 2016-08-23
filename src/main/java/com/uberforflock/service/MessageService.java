package com.uberforflock.service;

import co.flock.www.FlockApiClient;
import co.flock.www.model.flockevents.PressButton;
import co.flock.www.model.flockevents.SlashCommand;
import co.flock.www.model.messages.Attachments.*;
import co.flock.www.model.messages.FlockMessage;
import co.flock.www.model.messages.Message;
import com.uberforflock.dao.UserTokenDao;
import com.uberforflock.model.Availability;
import com.uberforflock.model.Ride;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by kumarke on 8/23/16.
 */
@Service
public class MessageService {

    @Autowired
    private UserTokenDao userTokenDao;

    private void sendRideMessage(Ride ride, PressButton pressButton){

    }

    public void sendAvailabilityMessage(String lat, String lon, Availability availability, SlashCommand slashCommand) throws  Exception{
        String userToken = userTokenDao.getUserToken(slashCommand.getUserId());
        FlockApiClient flockApiClient = new FlockApiClient(userToken,false);

        Message message = new Message(slashCommand.getChat(),availability.getTimes().size() > 0 ? "Which one you would like to book ?" : "Sorry no uber now");
        message.setAppId("d21753b9-c55b-4514-88a5-5c199c1b7801");
        if(availability.getTimes().size() > 0) {
            Attachment[] attachments = new Attachment[1];
            Attachment attachment = new Attachment();
            HtmlView htmlView = new HtmlView();
            StringBuilder sb = new StringBuilder();
            String cssBody = "<style type=\"text/css\">.light { color : #737373; } body {margin:0;padding:0;color: #333333;} ul {margin:0;padding: 5px 0 0 20px;} li {line-height:20px} a:link {color: #3B5998;} a:visited {color: #5796DD;} a:hover {color: #3A5998;}</style>";
            String fontStyle = "font-family:Lucida Grande,Arial,sans-serif;font-size:14px;line-height:20px";
            sb.append(cssBody);
            sb.append("<div style=\"" + fontStyle + "\">");
            sb.append("<table width=\"350\">");
            for (Availability.Times times : availability.getTimes()) {
                sb.append("<tr><td style=\"text-align: center;\"><img src=\"http://d1a3f4spazzrp4.cloudfront.net/car-types/mono/mono-ubergo.png\" alt=\"\" width=\"25\" height=\"25\" /></td><td><b>" + times.getLocalized_display_name() + "</b></td> <td>" + (times.getEstimate() / 60) + " minutes</td></tr>");
            }
            sb.append("</table>");
            sb.append("</div>");
            htmlView.setInline(sb.toString());
            htmlView.setHeight(30 * availability.getTimes().size());
            View view = new View();
            view.setHtml(htmlView);

            int totalButtons = availability.getTimes().size() > 3 ? 3 : availability.getTimes().size();

            Button[] buttons = new Button[totalButtons];
            for(int i = 0 ; i < totalButtons ; i++) {
                Button button = new Button();
                button.setIcon("http://d1a3f4spazzrp4.cloudfront.net/car-types/mono/mono-uberpool.png");
                button.setName("Book " + availability.getTimes().get(i).getLocalized_display_name());
                button.setId(availability.getTimes().get(i).getProduct_id());
                Action sendToAppService = new Action();
                sendToAppService.addDispatchEvent();
                button.setAction(sendToAppService);
                buttons[i] = button;
            }

            attachment.setButtons(buttons);

            attachment.setViews(view);
            attachments[0] = attachment;
            message.setAttachments(attachments);

        }

        String id = flockApiClient.chatSendMessage(new FlockMessage(message));
    }
}
