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
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|FileStatus
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
name|FileSystem
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
name|BlockLocation
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
name|io
operator|.
name|Text
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
name|io
operator|.
name|Text
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
name|mapred
operator|.
name|lib
operator|.
name|CombineFileSplit
import|;
end_import

begin_comment
comment|/**  * A sub-collection of input files. Unlike {@link FileSplit}, MultiFileSplit   * class does not represent a split of a file, but a split of input files   * into smaller sets. The atomic unit of split is a file.<br>   * MultiFileSplit can be used to implement {@link RecordReader}'s, with   * reading one record per file.  * @see FileSplit  * @see MultiFileInputFormat   * @deprecated Use {@link org.apache.hadoop.mapred.lib.CombineFileSplit} instead  */
end_comment

begin_class
annotation|@
name|Deprecated
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|class|MultiFileSplit
specifier|public
class|class
name|MultiFileSplit
extends|extends
name|CombineFileSplit
block|{
DECL|method|MultiFileSplit ()
name|MultiFileSplit
parameter_list|()
block|{}
DECL|method|MultiFileSplit (JobConf job, Path[] files, long[] lengths)
specifier|public
name|MultiFileSplit
parameter_list|(
name|JobConf
name|job
parameter_list|,
name|Path
index|[]
name|files
parameter_list|,
name|long
index|[]
name|lengths
parameter_list|)
block|{
name|super
argument_list|(
name|job
argument_list|,
name|files
argument_list|,
name|lengths
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
name|HashSet
argument_list|<
name|String
argument_list|>
name|hostSet
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Path
name|file
range|:
name|getPaths
argument_list|()
control|)
block|{
name|FileSystem
name|fs
init|=
name|file
operator|.
name|getFileSystem
argument_list|(
name|getJob
argument_list|()
argument_list|)
decl_stmt|;
name|FileStatus
name|status
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|BlockLocation
index|[]
name|blkLocations
init|=
name|fs
operator|.
name|getFileBlockLocations
argument_list|(
name|status
argument_list|,
literal|0
argument_list|,
name|status
operator|.
name|getLen
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|blkLocations
operator|!=
literal|null
operator|&&
name|blkLocations
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|addToSet
argument_list|(
name|hostSet
argument_list|,
name|blkLocations
index|[
literal|0
index|]
operator|.
name|getHosts
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|hostSet
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|hostSet
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
DECL|method|addToSet (Set<String> set, String[] array)
specifier|private
name|void
name|addToSet
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|set
parameter_list|,
name|String
index|[]
name|array
parameter_list|)
block|{
for|for
control|(
name|String
name|s
range|:
name|array
control|)
name|set
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|getPaths
argument_list|()
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|getPath
argument_list|(
name|i
argument_list|)
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
operator|+
literal|":0+"
operator|+
name|getLength
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|<
name|getPaths
argument_list|()
operator|.
name|length
operator|-
literal|1
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

