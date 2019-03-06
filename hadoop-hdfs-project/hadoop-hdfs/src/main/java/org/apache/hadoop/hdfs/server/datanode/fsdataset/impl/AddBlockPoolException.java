begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode.fsdataset.impl
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
name|datanode
operator|.
name|fsdataset
operator|.
name|impl
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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
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
name|datanode
operator|.
name|fsdataset
operator|.
name|FsVolumeSpi
import|;
end_import

begin_comment
comment|/**  * This exception collects all IOExceptions thrown when adding block pools and  * scanning volumes. It keeps the information about which volume is associated  * with an exception.  *  */
end_comment

begin_class
DECL|class|AddBlockPoolException
specifier|public
class|class
name|AddBlockPoolException
extends|extends
name|IOException
block|{
DECL|field|unhealthyDataDirs
specifier|private
name|Map
argument_list|<
name|FsVolumeSpi
argument_list|,
name|IOException
argument_list|>
name|unhealthyDataDirs
decl_stmt|;
DECL|method|AddBlockPoolException (Map<FsVolumeSpi, IOException> unhealthyDataDirs)
specifier|public
name|AddBlockPoolException
parameter_list|(
name|Map
argument_list|<
name|FsVolumeSpi
argument_list|,
name|IOException
argument_list|>
name|unhealthyDataDirs
parameter_list|)
block|{
name|this
operator|.
name|unhealthyDataDirs
operator|=
name|unhealthyDataDirs
expr_stmt|;
block|}
DECL|method|getFailingVolumes ()
specifier|public
name|Map
argument_list|<
name|FsVolumeSpi
argument_list|,
name|IOException
argument_list|>
name|getFailingVolumes
parameter_list|()
block|{
return|return
name|unhealthyDataDirs
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|": "
operator|+
name|unhealthyDataDirs
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

