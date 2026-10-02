import React from 'react';

export default function StatCard({ title, value, subtitle, icon, highlightColor }) {
  return (
    <div className="glass-card" style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
      <div>
        <div style={{ color: 'var(--text-muted)', fontSize: '0.85rem', fontWeight: 600, marginBottom: '4px' }}>
          {title}
        </div>
        <div style={{ fontSize: '1.8rem', fontWeight: 800, color: highlightColor || 'var(--text-main)' }}>
          {value}
        </div>
        {subtitle && (
          <div style={{ fontSize: '0.75rem', color: 'var(--text-dim)', marginTop: '4px' }}>
            {subtitle}
          </div>
        )}
      </div>
      {icon && (
        <div style={{
          fontSize: '2rem',
          padding: '12px',
          borderRadius: '14px',
          background: 'rgba(255, 255, 255, 0.05)',
          border: '1px solid var(--border-color)'
        }}>
          {icon}
        </div>
      )}
    </div>
  );
}
