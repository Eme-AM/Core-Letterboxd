import React from "react";
import "./ConfirmDeleteModal.css";

function ConfirmDeleteModal({ onConfirm, onCancel, itemName = "this item" }) {
  return (
    <div className="modal-overlay">
      <div className="modal-container small">
        <header className="modal-header">
          <h2 className="modal-title">Delete Confirmation</h2>
          <button className="modal-close" onClick={onCancel}>✕</button>
        </header>

        <p className="modal-subtitle">
          Are you sure you want to delete <b>{itemName}</b>?<br />
          This action cannot be undone.
        </p>

        <div className="modal-actions">
          <button className="btn cancel" onClick={onCancel}>Cancel</button>
          <button className="btn delete" onClick={onConfirm}>Delete</button>
        </div>
      </div>
    </div>
  );
}

export default ConfirmDeleteModal;
