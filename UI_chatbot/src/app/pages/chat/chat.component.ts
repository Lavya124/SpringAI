import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SidebarComponent } from '../../components/sidebar/sidebar.component';
import { ChatWindowComponent } from '../../components/chat-window/chat-window.component';
import { MessageInputComponent } from '../../components/message-input/message-input.component';
import { ProfilePanelComponent } from '../../components/profile-panel/profile-panel.component';
import { ChatService } from '../../services/chat.service';
@Component({
  selector: 'app-chat-page',
  standalone: true,
  imports: [
    CommonModule,
    SidebarComponent,
    ChatWindowComponent,
    MessageInputComponent,
    ProfilePanelComponent
  ],
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.css']
})
export class ChatComponent {
  currentUserId = 'lavya';
  currentChatId: string | null = null;
  messages: { sender: 'user'|'bot', text: string }[] = [];
  profile: {[k:string]:string} = {};

  constructor(private chatService: ChatService) {}

  onChatSelected(chatId: string) {
    this.currentChatId = chatId;
    // TODO: load chat history from backend (or localStorage) and profile
    const stored = localStorage.getItem('chat_'+chatId);
    this.messages = stored ? JSON.parse(stored) : [];
  }

  onNewChat(chatId: string) {
    this.currentChatId = chatId;
    this.messages = [];
  }

  onSend(text: string) {
  if (!this.currentChatId) return;

  // 1) Show user message immediately
  this.messages.push({ sender: 'user', text });

  // temporary store in localStorage
  localStorage.setItem('chat_' + this.currentChatId, JSON.stringify(this.messages));

  // 2) Call backend
  this.chatService.sendMessage(this.currentUserId, this.currentChatId, text)
    .subscribe(botReply => {

      // 3) Show bot reply
      this.messages.push({ sender: 'bot', text: botReply });

      // 4) Update localStorage
      localStorage.setItem('chat_' + this.currentChatId, JSON.stringify(this.messages));
    });
}

}
