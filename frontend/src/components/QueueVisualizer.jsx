import React from 'react';

export default function QueueVisualizer({ queue, onAction, actionLabel }) {
  if (!queue || queue.length === 0) {
    return (
      <div className="glass-card" style={{ textAlign: 'center', padding: '40px', color: 'var(--text-muted)' }}>
        <h3>Queue Empty</h3>
        <p style={{ fontSize: '0.85rem', marginTop: '6px' }}>No patients currently waiting in this department queue.</p>
      </div>
    );
  }

  return (
    <div className="glass-card">
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '16px' }}>
        <h3 style={{ fontSize: '1.1rem', fontWeight: 700 }}>Priority Queue (Max-Heap Live Order)</h3>
        <span className="badge badge-regular">{queue.length} Patients Waiting</span>
      </div>

      <table className="custom-table">
        <thead>
          <tr>
            <th>Pos</th>
            <th>Ticket</th>
            <th>Patient Name</th>
            <th>Age</th>
            <th>Severity</th>
            <th>Priority Score</th>
            <th>Est. Wait</th>
            {onAction && <th>Action</th>}
          </tr>
        </thead>
        <tbody>
          {queue.map((item, index) => {
            const isTop = index === 0;
            return (
              <tr key={item.id} style={{
                background: isTop ? 'rgba(16, 185, 129, 0.12)' : 'transparent',
                borderLeft: isTop ? '3px solid #10b981' : 'none'
              }}>
                <td style={{ fontWeight: 800, color: isTop ? '#34d399' : 'inherit' }}>
                  #{index + 1}
                </td>
                <td style={{ fontFamily: 'monospace', fontWeight: 700, color: 'var(--accent-cyan)' }}>
                  {item.ticketNumber}
                </td>
                <td style={{ fontWeight: 600 }}>{item.patientName}</td>
                <td>{item.patientAge || 'N/A'} yrs</td>
                <td>
                  <span className={`badge ${
                    item.severityLevel === 'EMERGENCY' ? 'badge-emergency' :
                    item.severityLevel === 'URGENT' ? 'badge-urgent' : 'badge-regular'
                  }`}>
                    {item.severityLevel}
                  </span>
                </td>
                <td style={{ fontWeight: 700, color: '#f59e0b' }}>
                  {Math.round(item.calculatedPriority)} pts
                </td>
                <td>{item.estimatedWaitMinutes} mins</td>
                {onAction && (
                  <td>
                    <button className="btn btn-primary" style={{ padding: '4px 12px', fontSize: '0.8rem' }} onClick={() => onAction(item)}>
                      {actionLabel || 'Select'}
                    </button>
                  </td>
                )}
              </tr>
            );
          })}
        </tbody>
      </table>
    </div>
  );
}
