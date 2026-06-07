import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ScoutRequestService } from '../../services/scout-request.service'; 

@Component({
  selector: 'app-scout-request-modal',
  templateUrl: './scout-request-modal.component.html',
  styleUrls: ['./scout-request-modal.component.css']
})
export class ScoutRequestModalComponent implements OnInit {
  @Input() playerId!: number;
  @Input() isOpen: boolean = false;
  @Output() close = new EventEmitter<void>();

  requestForm!: FormGroup;
  isSubmitting = false;

  constructor(
    private fb: FormBuilder,
    private scoutRequestService: ScoutRequestService
  ) {}

  ngOnInit(): void {
    this.requestForm = this.fb.group({
      instructions: ['', [Validators.required, Validators.minLength(10)]],
      deadline: ['', Validators.required]
    });
  }

  onClose(): void {
    this.requestForm.reset();
    this.close.emit();
  }

  onSubmit(): void {
    if (this.requestForm.invalid) return;

    this.isSubmitting = true;
    const payload = {
      playerId: this.playerId,
      instructions: this.requestForm.value.instructions,
      deadline: this.requestForm.value.deadline
    };

    this.scoutRequestService.saveScoutRequest(payload).subscribe({
      next: () => {
        this.isSubmitting = false;
        alert('Skauting zahtev je uspešno kreiran!');
        this.onClose();
      },
      error: (err) => {
        console.error(err);
        alert('Došlo je do greške prilikom kreiranja zahteva.');
        this.isSubmitting = false;
      }
    });
  }
}