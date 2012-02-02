begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
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
name|io
operator|.
name|DataInput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutput
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
name|classification
operator|.
name|InterfaceAudience
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
name|classification
operator|.
name|InterfaceStability
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
name|fs
operator|.
name|Path
import|;
end_import

begin_comment
comment|/** A section of an input file.  Returned by {@link  * InputFormat#getSplits(JobConf, int)} and passed to  * {@link InputFormat#getRecordReader(InputSplit,JobConf,Reporter)}.   */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|class|FileSplit
specifier|public
class|class
name|FileSplit
extends|extends
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|InputSplit
implements|implements
name|InputSplit
block|{
DECL|field|fs
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|lib
operator|.
name|input
operator|.
name|FileSplit
name|fs
decl_stmt|;
DECL|method|FileSplit ()
specifier|protected
name|FileSplit
parameter_list|()
block|{
name|fs
operator|=
operator|new
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|lib
operator|.
name|input
operator|.
name|FileSplit
argument_list|()
expr_stmt|;
block|}
comment|/** Constructs a split.    * @deprecated    * @param file the file name    * @param start the position of the first byte in the file to process    * @param length the number of bytes in the file to process    */
annotation|@
name|Deprecated
DECL|method|FileSplit (Path file, long start, long length, JobConf conf)
specifier|public
name|FileSplit
parameter_list|(
name|Path
name|file
parameter_list|,
name|long
name|start
parameter_list|,
name|long
name|length
parameter_list|,
name|JobConf
name|conf
parameter_list|)
block|{
name|this
argument_list|(
name|file
argument_list|,
name|start
argument_list|,
name|length
argument_list|,
operator|(
name|String
index|[]
operator|)
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/** Constructs a split with host information    *    * @param file the file name    * @param start the position of the first byte in the file to process    * @param length the number of bytes in the file to process    * @param hosts the list of hosts containing the block, possibly null    */
DECL|method|FileSplit (Path file, long start, long length, String[] hosts)
specifier|public
name|FileSplit
parameter_list|(
name|Path
name|file
parameter_list|,
name|long
name|start
parameter_list|,
name|long
name|length
parameter_list|,
name|String
index|[]
name|hosts
parameter_list|)
block|{
name|fs
operator|=
operator|new
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|lib
operator|.
name|input
operator|.
name|FileSplit
argument_list|(
name|file
argument_list|,
name|start
argument_list|,
name|length
argument_list|,
name|hosts
argument_list|)
expr_stmt|;
block|}
DECL|method|FileSplit (org.apache.hadoop.mapreduce.lib.input.FileSplit fs)
specifier|public
name|FileSplit
parameter_list|(
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|lib
operator|.
name|input
operator|.
name|FileSplit
name|fs
parameter_list|)
block|{
name|this
operator|.
name|fs
operator|=
name|fs
expr_stmt|;
block|}
comment|/** The file containing this split's data. */
DECL|method|getPath ()
specifier|public
name|Path
name|getPath
parameter_list|()
block|{
return|return
name|fs
operator|.
name|getPath
argument_list|()
return|;
block|}
comment|/** The position of the first byte in the file to process. */
DECL|method|getStart ()
specifier|public
name|long
name|getStart
parameter_list|()
block|{
return|return
name|fs
operator|.
name|getStart
argument_list|()
return|;
block|}
comment|/** The number of bytes in the file to process. */
DECL|method|getLength ()
specifier|public
name|long
name|getLength
parameter_list|()
block|{
return|return
name|fs
operator|.
name|getLength
argument_list|()
return|;
block|}
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|fs
operator|.
name|toString
argument_list|()
return|;
block|}
comment|////////////////////////////////////////////
comment|// Writable methods
comment|////////////////////////////////////////////
DECL|method|write (DataOutput out)
specifier|public
name|void
name|write
parameter_list|(
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|fs
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
DECL|method|readFields (DataInput in)
specifier|public
name|void
name|readFields
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|fs
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
DECL|method|getLocations ()
specifier|public
name|String
index|[]
name|getLocations
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|fs
operator|.
name|getLocations
argument_list|()
return|;
block|}
block|}
end_class

end_unit

