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

begin_enum
DECL|enum|QueueACL
specifier|public
enum|enum
name|QueueACL
block|{
DECL|enumConstant|SUBMIT_JOB
name|SUBMIT_JOB
block|,
DECL|enumConstant|ADMINISTER_QUEUE
name|ADMINISTER_QUEUE
block|,
DECL|enumConstant|ADMINISTER_JOBS
name|ADMINISTER_JOBS
block|;
comment|// currently unused
block|}
end_enum

end_unit

