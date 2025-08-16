import React, { useState } from 'react';
import { Bell, Settings, Pencil, Trash2, MoreVertical, View } from 'lucide-react';
import type { MonitorDTO } from '../types/MonitorDTO';
import { Card } from './ui/card';
import { Button } from './ui/button';

// Define the props for the MonitorCard component.
interface MonitorCardProps {
  monitor: MonitorDTO;
  onViewHistory: () => void;
  onViewConfig: () => void;
  onUpdateMonitor: () => void;
  onDeleteMonitor: () => void;
  onViewChecks: () => void;
}

/**
 * A reusable card component to display a single uptime monitor's status and details.
 * It's designed to be used within a list on a dashboard.
 *
 * @param {MonitorCardProps} { monitor } - The data object for the monitor to display.
 */
export const MonitorCard: React.FC<MonitorCardProps> = ({
  monitor,
  onViewHistory,
  onViewConfig,
  onUpdateMonitor,
  onDeleteMonitor,
  onViewChecks,
}) => {
  const [showActions, setShowActions] = useState(false);

  const statusColorClasses = {
    UP: 'text-green-600 bg-green-50 border-green-200',
    DOWN: 'text-red-600 bg-red-50 border-red-200',
    UNKNOWN: 'text-gray-600 bg-gray-50 border-gray-200',
    PAUSED: 'text-yellow-600 bg-yellow-50 border-yellow-200',
  };

  const lastChecked = monitor.lastCheckedAt
    ? new Date(monitor.lastCheckedAt).toLocaleString()
    : 'Never';

  // Truncate URL if too long
  const displayUrl = monitor.url.length > 40 
    ? `${monitor.url.substring(0, 37)}...` 
    : monitor.url;

  return (
    <Card className="relative p-6 transition-all duration-200 hover:shadow-lg hover:-translate-y-1 border border-gray-200">
      {/* Header with URL and Status */}
      <div className="flex items-start justify-between mb-4">
        <div className="flex-1 min-w-0">
          <h3 
            className="text-lg font-semibold text-gray-900 mb-2 truncate" 
            title={monitor.url}
          >
            {displayUrl}
          </h3>
          <span className={`inline-flex items-center px-2.5 py-0.5 text-xs font-medium rounded-full border ${statusColorClasses[monitor.status]}`}>
            <div className={`w-2 h-2 rounded-full mr-1.5 ${monitor.status === 'UP' ? 'bg-green-500' : monitor.status === 'DOWN' ? 'bg-red-500' : monitor.status === 'PAUSED' ? 'bg-yellow-500' : 'bg-gray-500'}`}></div>
            {monitor.status}
          </span>
        </div>
        
        {/* Action Menu */}
        <div className="relative ml-4 flex-shrink-0">
          <Button 
            onClick={() => setShowActions(!showActions)}
            variant="ghost" 
            className="p-2 h-8 w-8 text-gray-400 hover:text-gray-600 hover:bg-gray-100 rounded-full"
          >
            <MoreVertical className="w-4 h-4" />
          </Button>
          
          {showActions && (
            <div className="absolute right-0 top-10 z-10 bg-white rounded-lg shadow-lg border border-gray-200 py-1 w-32">
              <button 
                onClick={() => { onUpdateMonitor(); setShowActions(false); }}
                className="flex items-center w-full px-3 py-2 text-sm text-gray-700 hover:bg-gray-50"
              >
                <Pencil className="w-4 h-4 mr-2" />
                Edit
              </button>

              <button
                onClick={() => {onViewChecks(); setShowActions(false);}}
                className="flex items-center w-full px-3 py-2 text-sm text-blue-600 hover:bg-blue-50"
              >
                <View className="w-4 h-4 mr-2" />
                Checks
              </button>

              <button 
                onClick={() => { onDeleteMonitor(); setShowActions(false); }}
                className="flex items-center w-full px-3 py-2 text-sm text-red-600 hover:bg-red-50"
              >
                <Trash2 className="w-4 h-4 mr-2" />
                Delete
              </button>

              
            </div>
          )}
        </div>
      </div>

      {/* Monitor Details */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-3 mb-4">
        <div className="text-sm">
          <span className="text-gray-500 block">Type</span>
          <span className="font-medium text-gray-900">{monitor.type}</span>
        </div>
        <div className="text-sm">
          <span className="text-gray-500 block">Interval</span>
          <span className="font-medium text-gray-900">{monitor.checkIntervalSeconds}s</span>
        </div>
        <div className="text-sm">
          <span className="text-gray-500 block">Last Checked</span>
          <span className="font-medium text-gray-900" title={lastChecked}>
            {lastChecked.length > 15 ? `${lastChecked.substring(0, 12)}...` : lastChecked}
          </span>
        </div>
      </div>

      {/* Action Buttons */}
      <div className="flex gap-2">
        <Button 
          onClick={onViewHistory} 
          variant="outline" 
          className="flex-1 h-9 text-sm font-medium border-gray-300 hover:border-gray-400 hover:bg-gray-50"
        >
          <Bell className="w-4 h-4 mr-2" />
          History
        </Button>
        
        <Button 
          onClick={onViewConfig} 
          className="flex-1 h-9 text-sm font-medium bg-blue-600 hover:bg-blue-700 text-white shadow-sm"
        >
          <Settings className="w-4 h-4 mr-2" />
          Config
        </Button>
      </div>

      {/* Click outside to close actions menu */}
      {showActions && (
        <div 
          className="fixed inset-0 z-0" 
          onClick={() => setShowActions(false)}
        />
      )}
    </Card>
  );
};