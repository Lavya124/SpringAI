import { Component, Input, OnChanges, SimpleChanges, ViewChild, ElementRef, AfterViewChecked } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-chat-window',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './chat-window.component.html',
  styleUrls: ['./chat-window.component.css']
})
export class ChatWindowComponent implements OnChanges, AfterViewChecked {
  @Input() messages: { sender: 'user'|'bot', text: string }[] = [];
  @ViewChild('scrollArea') private scrollArea!: ElementRef;

  ngOnChanges(changes: SimpleChanges) {
    // when messages change, scroll to bottom (handled in AfterViewChecked)
  }

  ngAfterViewChecked() {
    this.scrollToBottom();
  }

  private scrollToBottom(): void {
    try {
      this.scrollArea.nativeElement.scrollTop = this.scrollArea.nativeElement.scrollHeight;
    } catch (err) {}
  }
}
