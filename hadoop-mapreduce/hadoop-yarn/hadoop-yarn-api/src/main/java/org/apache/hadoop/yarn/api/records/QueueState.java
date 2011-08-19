begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.hadoop.yarn.api.records
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|api
operator|.
name|records
package|;
end_package

begin_comment
comment|/**  * State of a Queue  */
end_comment

begin_enum
DECL|enum|QueueState
specifier|public
enum|enum
name|QueueState
block|{
DECL|enumConstant|STOPPED
name|STOPPED
block|,
DECL|enumConstant|RUNNING
name|RUNNING
block|}
end_enum

end_unit

