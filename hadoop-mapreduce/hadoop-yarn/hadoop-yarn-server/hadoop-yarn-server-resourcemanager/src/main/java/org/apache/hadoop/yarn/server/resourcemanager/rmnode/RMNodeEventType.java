begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.rmnode
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|rmnode
package|;
end_package

begin_enum
DECL|enum|RMNodeEventType
specifier|public
enum|enum
name|RMNodeEventType
block|{
comment|// Source: AdminService
DECL|enumConstant|DECOMMISSION
name|DECOMMISSION
block|,
comment|// ResourceTrackerService
DECL|enumConstant|STATUS_UPDATE
name|STATUS_UPDATE
block|,
DECL|enumConstant|REBOOTING
name|REBOOTING
block|,
comment|// Source: Application
DECL|enumConstant|CLEANUP_APP
name|CLEANUP_APP
block|,
comment|// Source: Container
DECL|enumConstant|CONTAINER_ALLOCATED
name|CONTAINER_ALLOCATED
block|,
DECL|enumConstant|CLEANUP_CONTAINER
name|CLEANUP_CONTAINER
block|,
comment|// Source: NMLivelinessMonitor
DECL|enumConstant|EXPIRE
name|EXPIRE
block|}
end_enum

end_unit

