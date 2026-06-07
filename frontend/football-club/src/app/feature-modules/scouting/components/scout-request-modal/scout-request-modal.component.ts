import { Component, Input, Output, EventEmitter, OnInit, OnChanges, SimpleChanges } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ScoutRequestService } from '../../services/scout-request.service';
import { ScoutRequest, ScoutRequestSave } from '../../models/scout-request.model';

@Component({
  selector: 'app-scout-request-modal',
  templateUrl: './scout-request-modal.component.html',
  styleUrls: ['./scout-request-modal.component.css']
})
export class ScoutRequestModalComponent implements OnInit, OnChanges {
  @Input() playerId?: number;
  @Input() requestToEdit?: ScoutRequest | null = null;
  @Input() isOpen: boolean = false;
  @Output() close = new EventEmitter<boolean>();

  requestForm!: FormGroup;
  isSubmitting = false;

  constructor(
    private fb: FormBuilder,
    private scoutRequestService: ScoutRequestService
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.fillFormIfEditMode();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (this.requestForm && changes['isOpen'] && this.isOpen) {
      this.requestForm.reset();
      this.fillFormIfEditMode();
    }
  }

  initForm(): void {
    if (!this.requestForm) {
      this.requestForm = this.fb.group({
        instructions: ['', [Validators.required, Validators.minLength(10)]],
        deadline: ['', Validators.required]
      });
    }
  }

  fillFormIfEditMode(): void {
    if (this.requestToEdit) {
      const formattedDate = this.requestToEdit.deadline ? this.requestToEdit.deadline.slice(0, 16) : ''; 
      this.requestForm.patchValue({
        instructions: this.requestToEdit.instructions,
        deadline: formattedDate
      });
    }
  }

  onClose(saved: boolean = false): void {
    this.requestForm.reset();
    this.close.emit(saved);
  }

  onSubmit(): void {
    if (this.requestForm.invalid) return;

    this.isSubmitting = true;
    const targetPlayerId = this.requestToEdit ? this.requestToEdit.playerId : this.playerId;
    
    const payload: ScoutRequestSave = {
      playerId: targetPlayerId!,
      instructions: this.requestForm.value.instructions,
      deadline: this.requestForm.value.deadline
    };

    const request$ = this.requestToEdit 
      ? this.scoutRequestService.updateRequest(this.requestToEdit.id, payload)
      : this.scoutRequestService.saveScoutRequest(payload);

    request$.subscribe({
      next: () => {
        this.isSubmitting = false;
        alert(this.requestToEdit ? 'Zahtev je uspešno izmenjen!' : 'Skauting zahtev je uspešno kreiran!');
        this.onClose(true);
      },
      error: (err) => {
        console.error(err);
        alert('Došlo je do greške prilikom čuvanja zahteva.');
        this.isSubmitting = false;
      }
    });
  }
}