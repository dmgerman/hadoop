begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|namenode
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
import|;
end_import

begin_comment
comment|/**  * Used to inject certain faults for testing.  */
end_comment

begin_class
DECL|class|EncryptionFaultInjector
specifier|public
class|class
name|EncryptionFaultInjector
block|{
annotation|@
name|VisibleForTesting
DECL|field|instance
specifier|public
specifier|static
name|EncryptionFaultInjector
name|instance
init|=
operator|new
name|EncryptionFaultInjector
argument_list|()
decl_stmt|;
annotation|@
name|VisibleForTesting
DECL|method|getInstance ()
specifier|public
specifier|static
name|EncryptionFaultInjector
name|getInstance
parameter_list|()
block|{
return|return
name|instance
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|startFileAfterGenerateKey ()
specifier|public
name|void
name|startFileAfterGenerateKey
parameter_list|()
throws|throws
name|IOException
block|{}
block|}
end_class

end_unit

