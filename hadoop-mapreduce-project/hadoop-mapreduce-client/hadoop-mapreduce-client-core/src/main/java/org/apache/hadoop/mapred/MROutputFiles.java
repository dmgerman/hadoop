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
name|conf
operator|.
name|Configuration
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
name|conf
operator|.
name|Configurable
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
name|LocalDirAllocator
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
name|mapreduce
operator|.
name|MRConfig
import|;
end_import

begin_comment
comment|/**  * Manipulate the working area for the transient store for maps and reduces.  *  * This class is used by map and reduce tasks to identify the directories that  * they need to write to/read from for intermediate files. The callers of  * these methods are from the Child running the Task.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|MROutputFiles
specifier|public
class|class
name|MROutputFiles
extends|extends
name|MapOutputFile
block|{
DECL|field|lDirAlloc
specifier|private
name|LocalDirAllocator
name|lDirAlloc
init|=
operator|new
name|LocalDirAllocator
argument_list|(
name|MRConfig
operator|.
name|LOCAL_DIR
argument_list|)
decl_stmt|;
DECL|method|MROutputFiles ()
specifier|public
name|MROutputFiles
parameter_list|()
block|{   }
comment|/**    * Return the path to local map output file created earlier    *    * @return path    * @throws IOException    */
annotation|@
name|Override
DECL|method|getOutputFile ()
specifier|public
name|Path
name|getOutputFile
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|lDirAlloc
operator|.
name|getLocalPathToRead
argument_list|(
name|Constants
operator|.
name|OUTPUT
operator|+
name|Path
operator|.
name|SEPARATOR
operator|+
name|MAP_OUTPUT_FILENAME_STRING
argument_list|,
name|getConf
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Create a local map output file name.    *    * @param size the size of the file    * @return path    * @throws IOException    */
annotation|@
name|Override
DECL|method|getOutputFileForWrite (long size)
specifier|public
name|Path
name|getOutputFileForWrite
parameter_list|(
name|long
name|size
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|lDirAlloc
operator|.
name|getLocalPathForWrite
argument_list|(
name|Constants
operator|.
name|OUTPUT
operator|+
name|Path
operator|.
name|SEPARATOR
operator|+
name|MAP_OUTPUT_FILENAME_STRING
argument_list|,
name|size
argument_list|,
name|getConf
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Create a local map output file name on the same volume.    */
annotation|@
name|Override
DECL|method|getOutputFileForWriteInVolume (Path existing)
specifier|public
name|Path
name|getOutputFileForWriteInVolume
parameter_list|(
name|Path
name|existing
parameter_list|)
block|{
return|return
operator|new
name|Path
argument_list|(
name|existing
operator|.
name|getParent
argument_list|()
argument_list|,
name|MAP_OUTPUT_FILENAME_STRING
argument_list|)
return|;
block|}
comment|/**    * Return the path to a local map output index file created earlier    *    * @return path    * @throws IOException    */
annotation|@
name|Override
DECL|method|getOutputIndexFile ()
specifier|public
name|Path
name|getOutputIndexFile
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|lDirAlloc
operator|.
name|getLocalPathToRead
argument_list|(
name|Constants
operator|.
name|OUTPUT
operator|+
name|Path
operator|.
name|SEPARATOR
operator|+
name|MAP_OUTPUT_FILENAME_STRING
operator|+
name|MAP_OUTPUT_INDEX_SUFFIX_STRING
argument_list|,
name|getConf
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Create a local map output index file name.    *    * @param size the size of the file    * @return path    * @throws IOException    */
annotation|@
name|Override
DECL|method|getOutputIndexFileForWrite (long size)
specifier|public
name|Path
name|getOutputIndexFileForWrite
parameter_list|(
name|long
name|size
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|lDirAlloc
operator|.
name|getLocalPathForWrite
argument_list|(
name|Constants
operator|.
name|OUTPUT
operator|+
name|Path
operator|.
name|SEPARATOR
operator|+
name|MAP_OUTPUT_FILENAME_STRING
operator|+
name|MAP_OUTPUT_INDEX_SUFFIX_STRING
argument_list|,
name|size
argument_list|,
name|getConf
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Create a local map output index file name on the same volume.    */
annotation|@
name|Override
DECL|method|getOutputIndexFileForWriteInVolume (Path existing)
specifier|public
name|Path
name|getOutputIndexFileForWriteInVolume
parameter_list|(
name|Path
name|existing
parameter_list|)
block|{
return|return
operator|new
name|Path
argument_list|(
name|existing
operator|.
name|getParent
argument_list|()
argument_list|,
name|MAP_OUTPUT_FILENAME_STRING
operator|+
name|MAP_OUTPUT_INDEX_SUFFIX_STRING
argument_list|)
return|;
block|}
comment|/**    * Return a local map spill file created earlier.    *    * @param spillNumber the number    * @return path    * @throws IOException    */
annotation|@
name|Override
DECL|method|getSpillFile (int spillNumber)
specifier|public
name|Path
name|getSpillFile
parameter_list|(
name|int
name|spillNumber
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|lDirAlloc
operator|.
name|getLocalPathToRead
argument_list|(
name|Constants
operator|.
name|OUTPUT
operator|+
literal|"/spill"
operator|+
name|spillNumber
operator|+
literal|".out"
argument_list|,
name|getConf
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Create a local map spill file name.    *    * @param spillNumber the number    * @param size the size of the file    * @return path    * @throws IOException    */
annotation|@
name|Override
DECL|method|getSpillFileForWrite (int spillNumber, long size)
specifier|public
name|Path
name|getSpillFileForWrite
parameter_list|(
name|int
name|spillNumber
parameter_list|,
name|long
name|size
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|lDirAlloc
operator|.
name|getLocalPathForWrite
argument_list|(
name|Constants
operator|.
name|OUTPUT
operator|+
literal|"/spill"
operator|+
name|spillNumber
operator|+
literal|".out"
argument_list|,
name|size
argument_list|,
name|getConf
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Return a local map spill index file created earlier    *    * @param spillNumber the number    * @return path    * @throws IOException    */
annotation|@
name|Override
DECL|method|getSpillIndexFile (int spillNumber)
specifier|public
name|Path
name|getSpillIndexFile
parameter_list|(
name|int
name|spillNumber
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|lDirAlloc
operator|.
name|getLocalPathToRead
argument_list|(
name|Constants
operator|.
name|OUTPUT
operator|+
literal|"/spill"
operator|+
name|spillNumber
operator|+
literal|".out.index"
argument_list|,
name|getConf
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Create a local map spill index file name.    *    * @param spillNumber the number    * @param size the size of the file    * @return path    * @throws IOException    */
annotation|@
name|Override
DECL|method|getSpillIndexFileForWrite (int spillNumber, long size)
specifier|public
name|Path
name|getSpillIndexFileForWrite
parameter_list|(
name|int
name|spillNumber
parameter_list|,
name|long
name|size
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|lDirAlloc
operator|.
name|getLocalPathForWrite
argument_list|(
name|Constants
operator|.
name|OUTPUT
operator|+
literal|"/spill"
operator|+
name|spillNumber
operator|+
literal|".out.index"
argument_list|,
name|size
argument_list|,
name|getConf
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Return a local reduce input file created earlier    *    * @param mapId a map task id    * @return path    * @throws IOException    */
annotation|@
name|Override
DECL|method|getInputFile (int mapId)
specifier|public
name|Path
name|getInputFile
parameter_list|(
name|int
name|mapId
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|lDirAlloc
operator|.
name|getLocalPathToRead
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|REDUCE_INPUT_FILE_FORMAT_STRING
argument_list|,
name|Constants
operator|.
name|OUTPUT
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
name|mapId
argument_list|)
argument_list|)
argument_list|,
name|getConf
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Create a local reduce input file name.    *    * @param mapId a map task id    * @param size the size of the file    * @return path    * @throws IOException    */
annotation|@
name|Override
DECL|method|getInputFileForWrite (org.apache.hadoop.mapreduce.TaskID mapId, long size)
specifier|public
name|Path
name|getInputFileForWrite
parameter_list|(
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|TaskID
name|mapId
parameter_list|,
name|long
name|size
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|lDirAlloc
operator|.
name|getLocalPathForWrite
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|REDUCE_INPUT_FILE_FORMAT_STRING
argument_list|,
name|Constants
operator|.
name|OUTPUT
argument_list|,
name|mapId
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|,
name|size
argument_list|,
name|getConf
argument_list|()
argument_list|)
return|;
block|}
comment|/** Removes all of the files related to a task. */
annotation|@
name|Override
DECL|method|removeAll ()
specifier|public
name|void
name|removeAll
parameter_list|()
throws|throws
name|IOException
block|{
operator|(
operator|(
name|JobConf
operator|)
name|getConf
argument_list|()
operator|)
operator|.
name|deleteLocalFiles
argument_list|(
name|Constants
operator|.
name|OUTPUT
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setConf (Configuration conf)
specifier|public
name|void
name|setConf
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|conf
operator|instanceof
name|JobConf
operator|)
condition|)
block|{
name|conf
operator|=
operator|new
name|JobConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

