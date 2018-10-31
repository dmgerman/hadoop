begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.hadoop.ozone.s3.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|s3
operator|.
name|util
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceAudience
import|;
end_import

begin_comment
comment|/**  * Set of constants used for S3 implementation.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|S3Consts
specifier|public
specifier|final
class|class
name|S3Consts
block|{
comment|//Never Constructed
DECL|method|S3Consts ()
specifier|private
name|S3Consts
parameter_list|()
block|{    }
DECL|field|COPY_SOURCE_HEADER
specifier|public
specifier|static
specifier|final
name|String
name|COPY_SOURCE_HEADER
init|=
literal|"x-amz-copy-source"
decl_stmt|;
DECL|field|STORAGE_CLASS_HEADER
specifier|public
specifier|static
specifier|final
name|String
name|STORAGE_CLASS_HEADER
init|=
literal|"x-amz-storage-class"
decl_stmt|;
DECL|field|ENCODING_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|ENCODING_TYPE
init|=
literal|"url"
decl_stmt|;
block|}
end_class

end_unit

