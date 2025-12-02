import { Component, signal } from '@angular/core';
import { ChatComponent } from './pages/chat/chat.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [ChatComponent],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('ai-chat-ui');
}
