import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SearchTemplateManagementComponent } from './search-template-management.component';

describe('SearchTemplateManagementComponent', () => {
  let component: SearchTemplateManagementComponent;
  let fixture: ComponentFixture<SearchTemplateManagementComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SearchTemplateManagementComponent]
    });
    fixture = TestBed.createComponent(SearchTemplateManagementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
