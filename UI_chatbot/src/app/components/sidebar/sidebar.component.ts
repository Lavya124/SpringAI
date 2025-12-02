import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css']
})
export class SidebarComponent implements OnInit {

  chats: string[] = [];
  activeChatId: string | null = null;

  @Output() chatSelected = new EventEmitter<string>();
  @Output() newChatCreated = new EventEmitter<string>();

  ngOnInit(): void {
    // load from local for now; later load from backend
    const saved = localStorage.getItem('chat_list');
    this.chats = saved ? JSON.parse(saved) : [];
    this.activeChatId = this.chats.length ? this.chats[0] : null;
    if (this.activeChatId) this.chatSelected.emit(this.activeChatId);
  }

  selectChat(id: string) {
    this.activeChatId = id;
    this.chatSelected.emit(id);
  }

  createChat() {
    const id = 'chat-' + Date.now();
    this.chats.unshift(id);
    localStorage.setItem('chat_list', JSON.stringify(this.chats));
    this.newChatCreated.emit(id);
    this.selectChat(id);
  }
}
