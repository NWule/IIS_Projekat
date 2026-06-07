import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ScoutRequestModalComponent } from './scout-request-modal.component';

describe('ScoutRequestModalComponent', () => {
  let component: ScoutRequestModalComponent;
  let fixture: ComponentFixture<ScoutRequestModalComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ScoutRequestModalComponent]
    });
    fixture = TestBed.createComponent(ScoutRequestModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
