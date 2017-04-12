package edu.ouhk.comps380f.controller;

import edu.ouhk.comps380f.dao.TicketRepository;
import edu.ouhk.comps380f.model.Attachment;
import edu.ouhk.comps380f.model.ReplyTicket;
import edu.ouhk.comps380f.model.Ticket;
import edu.ouhk.comps380f.view.DownloadingView;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;
import edu.ouhk.comps380f.controller.PollController;
import edu.ouhk.comps380f.dao.AttachmentRepository;
import edu.ouhk.comps380f.dao.ReplyTicketRepository;

@Controller
@RequestMapping("ticket")
public class TicketController {
  
    @Autowired
    TicketRepository ticketRepo;
    
    @Autowired
    ReplyTicketRepository replyTicketRepo;
    
    @Autowired
    AttachmentRepository attachmentRepo; 
  
    private volatile int TICKET_ID_SEQUENCE = 1;

    public static Map<Long, ReplyTicket> replyTicketDatabase = new LinkedHashMap<>();

    @RequestMapping(value = {"", "list"}, method = RequestMethod.GET)
    public String list(ModelMap model) {
        model.addAttribute("ticketDatabase", ticketRepo.findAll());
     //   model.addAttribute("replyTicketDatabase",replyTicketDatabase );
        model.addAttribute("pollTicketDatabase", PollController.pollTicketDatabase);
        return "list";
    }
    

    @RequestMapping(value = "view/{ticketId}", method = RequestMethod.GET)
    public ModelAndView view(@PathVariable("ticketId") int ticketId) {
        Ticket ticket = this.ticketRepo.findById(ticketId);
        List<Attachment> attachmentlist = new ArrayList<Attachment>();
        
        for (Attachment a:attachmentRepo.findAll()){
            if (a.getId()==ticketId && a.getRid()==0){
                 attachmentlist.add(a);
            }
            
        } 
        List<ReplyTicket> replylist= new ArrayList<ReplyTicket>();
                      replylist=replyTicketRepo.findParts(ticketId);
                      
        for (Attachment a:attachmentRepo.findAll()){
           
                for (int i=0;i<replylist.size();i++){
                    
                    if (a.getRid()==replylist.get(i).getId()){
                        replylist.get(i).addAttachment(a);
                        //replylist.get(i).getId();     replyid
                        //a                             attachment
                        //reply.set(i, element)
                        
                    }
                }
                    
                  
            
            
        }
        
        if (ticket == null) {
            return new ModelAndView(new RedirectView("/ticket/list", true));
        }
        ModelAndView modelAndView = new ModelAndView("view");
        modelAndView.addObject("ticketId", ticketId);
        modelAndView.addObject("ticket", ticket);
        modelAndView.addObject("numberComment", replyTicketRepo.findParts(ticketId).size());
        //List<ReplyTicket> re=replyTicketRepo.findParts(ticketId);
        modelAndView.addObject("selectedReply",replylist);
        modelAndView.addObject("attachmentlist", attachmentlist);
        return modelAndView;
    }

    @RequestMapping(value = "create", method = RequestMethod.GET)
    public ModelAndView create() {
        return new ModelAndView("add", "ticketForm", new Form());
    }
    
    @RequestMapping(value = "listlecture", method = RequestMethod.GET)
    public String listlecture(ModelMap model) {
        String type="lecture";
        model.addAttribute("ticketDatabase", ticketRepo.findByCategories(type));
        return "listlecture";
    }
    @RequestMapping(value = "listlab", method = RequestMethod.GET)
    public String listlab(ModelMap model) {
        String type="lab";
        model.addAttribute("ticketDatabase", ticketRepo.findByCategories(type));
        return "listlab";
    }
    @RequestMapping(value = "listother", method = RequestMethod.GET)
    public String listother(ModelMap model) {
        String type="other";
        model.addAttribute("ticketDatabase", ticketRepo.findByCategories(type));
        return "listother";
    }

    public static class Form {
        private String subject;
        private String body;
        private List<MultipartFile> attachments;
        private List replyId;
        private String categories;

        public String getCategories() {
            return categories;
        }

        public void setCategories(String categories) {
            this.categories = categories;
        }

        public List getReplyId() {
            return replyId;
        }

        public void setReplyId(List replyId) {
            this.replyId = replyId;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }

        public List<MultipartFile> getAttachments() {
            return attachments;
        }

        public void setAttachments(List<MultipartFile> attachments) {
            this.attachments = attachments;
        }
    }
 
    @RequestMapping(value = "create", method = RequestMethod.POST)
    public View create(Form form, Principal principal) throws IOException {
Ticket ticket = new Ticket();
        //List<String> a= null;
        String a ="";
        if (ticket.getSubject() == null || ticket.getSubject().length() <= 0
                || ticket.getBody() == null || ticket.getBody().length() <= 0
                || ticket.getCategories() == null || ticket.getCategories().length() <= 0) {
            if(ticketRepo.findAll().isEmpty()){
              ticket.setId(1);
            }else{
                ticket.setId(ticketRepo.maxId()+1);
            }
            ticket.setCustomerName(principal.getName());
            ticket.setSubject(form.getSubject());
            ticket.setBody(form.getBody());
            ticket.setCategories(form.getCategories());
            int ticket_id = ticket.getId();
              for (MultipartFile filePart : form.getAttachments()) {
                System.out.println("here:" +filePart);
                Attachment attachment = new Attachment();
                attachment.setId(ticket_id);
                attachment.setRid(0);
                attachment.setName(filePart.getOriginalFilename());
                 //a.add(filePart.getOriginalFilename());
                a = attachment.getName();
                
                attachment.setMimeContentType(filePart.getContentType());
                attachment.setContents(filePart.getBytes());
                if (attachment.getName() != null && attachment.getName().length() > 0
                        && attachment.getContents() != null && attachment.getContents().length > 0) {
                    ticket.addAttachment(attachment);
                }
                if (!a.equals("")) {
                    attachmentRepo.createTicketAttachment(ticket.getAttachment(a));
                }
                
            }
        }
       // this.ticketDatabase.put(ticket.getId(), ticket);
           ticketRepo.create(ticket);
        
        //attachmentRepo.create(ticket.getAttachment(a));
        
        return new RedirectView("/ticket/view/" + ticketRepo.maxId(), true);
    }

    private synchronized long getNextTicketId() {
        return this.TICKET_ID_SEQUENCE++;
    }
    
    
    @RequestMapping(
            value = "/{ticketId}/attachment/{attachment:.+}",
            method = RequestMethod.GET
    )
    public View download(@PathVariable("ticketId") int ticketId,
            @PathVariable("attachment") String name) {
        Ticket ticket = this.ticketRepo.findById(ticketId);
        Attachment attachment= attachmentRepo.findByName(name);
        
        if (ticket != null) {
            
            if (attachment != null) {
                return new DownloadingView(attachment.getName(),
                        attachment.getMimeContentType(), attachment.getContents());
            }
        }
        return new RedirectView("/ticket/list", true);
    }

    @RequestMapping(
            value = "/{ticketId}/delete/{attachment:.+}",
            method = RequestMethod.GET
    )
    public View deleteAttachment(@PathVariable("ticketId") int ticketId,
            @PathVariable("attachment") String name) {
        Ticket ticket = this.ticketRepo.findById(ticketId);
        if (ticket != null) {
            if (ticket.hasAttachment(name)) {
                ticket.deleteAttachment(name);
            }
        }
        return new RedirectView("/ticket/edit/" + ticketId, true);
    }

    @RequestMapping(value = "edit/{ticketId}", method = RequestMethod.GET)
    public ModelAndView showEdit(@PathVariable("ticketId") int ticketId,
            Principal principal, HttpServletRequest request) {
        Ticket ticket = this.ticketRepo.findById(ticketId);
        if (ticket == null || 
                (!request.isUserInRole("ROLE_ADMIN")
                && !principal.getName().equals(ticket.getCustomerName()))) {
            return new ModelAndView(new RedirectView("/ticket/list", true));
        }

        ModelAndView modelAndView = new ModelAndView("edit");
        modelAndView.addObject("ticketId", Long.toString(ticketId));
        modelAndView.addObject("ticket", ticket);
        
        ModelAndView modelAndView2 = new ModelAndView("list");
        modelAndView2.addObject("ticketId2", Long.toString(ticketId));
        modelAndView2.addObject("ticket2", ticket);

        Form ticketForm = new Form();
        ticketForm.setSubject(ticket.getSubject());
        ticketForm.setBody(ticket.getBody());
        modelAndView.addObject("ticketForm", ticketForm);

        return modelAndView;
    }

    @RequestMapping(value = "edit/{ticketId}", method = RequestMethod.POST)
    public View edit(@PathVariable("ticketId") int ticketId, Form form,
            Principal principal, HttpServletRequest request)
            throws IOException {
        Ticket ticket = this.ticketRepo.findById(ticketId);
        if (ticket == null || 
                (!request.isUserInRole("ROLE_ADMIN")
                && !principal.getName().equals(ticket.getCustomerName()))) {
            return new RedirectView("/ticket/list", true);
        }
        ticket.setSubject(form.getSubject());
        ticket.setBody(form.getBody());

        for (MultipartFile filePart : form.getAttachments()) {
            Attachment attachment = new Attachment();
            attachment.setName(filePart.getOriginalFilename());
            attachment.setMimeContentType(filePart.getContentType());
            attachment.setContents(filePart.getBytes());
            if (attachment.getName() != null && attachment.getName().length() > 0
                    && attachment.getContents() != null && attachment.getContents().length > 0) {
                ticket.addAttachment(attachment);
            }
        }
        
        this.ticketRepo.update(ticket);
        return new RedirectView("/ticket/view/" + ticket.getId(), true);
    }

    @RequestMapping(value = "delete/{ticketId}", method = RequestMethod.GET)
    public View deleteTicket(@PathVariable("ticketId") int ticketId) {
        if (this.ticketRepo.findById(ticketId) != null) {
            this.ticketRepo.deleteById(ticketId);
        }
        return new RedirectView("/ticket/list", true);
    }

}
