begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred.nativetask.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|nativetask
operator|.
name|util
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
name|mapred
operator|.
name|TaskID
import|;
end_import

begin_comment
comment|/**  * base class of output files manager.  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|interface|NativeTaskOutput
specifier|public
interface|interface
name|NativeTaskOutput
block|{
comment|/**    * Return the path to local map output file created earlier    *     * @return path    * @throws IOException    */
DECL|method|getOutputFile ()
specifier|public
name|Path
name|getOutputFile
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Create a local map output file name.    *     * @param size    *          the size of the file    * @return path    * @throws IOException    */
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
function_decl|;
comment|/**    * Return the path to a local map output index file created earlier    *     * @return path    * @throws IOException    */
DECL|method|getOutputIndexFile ()
specifier|public
name|Path
name|getOutputIndexFile
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Create a local map output index file name.    *     * @param size    *          the size of the file    * @return path    * @throws IOException    */
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
function_decl|;
comment|/**    * Return a local map spill file created earlier.    *     * @param spillNumber    *          the number    * @return path    * @throws IOException    */
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
function_decl|;
comment|/**    * Create a local map spill file name.    *     * @param spillNumber    *          the number    * @param size    *          the size of the file    * @return path    * @throws IOException    */
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
function_decl|;
comment|/**    * Return a local map spill index file created earlier    *     * @param spillNumber    *          the number    * @return path    * @throws IOException    */
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
function_decl|;
comment|/**    * Create a local map spill index file name.    *     * @param spillNumber    *          the number    * @param size    *          the size of the file    * @return path    * @throws IOException    */
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
function_decl|;
comment|/**    * Return a local reduce input file created earlier    *     * @param mapId    *          a map task id    * @return path    * @throws IOException    */
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
function_decl|;
comment|/**    * Create a local reduce input file name.    *     * @param mapId    *          a map task id    * @param size    *          the size of the file    * @return path    * @throws IOException    */
DECL|method|getInputFileForWrite (TaskID mapId, long size, Configuration conf)
specifier|public
name|Path
name|getInputFileForWrite
parameter_list|(
name|TaskID
name|mapId
parameter_list|,
name|long
name|size
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Removes all of the files related to a task. */
DECL|method|removeAll ()
specifier|public
name|void
name|removeAll
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|getOutputName (int partition)
specifier|public
name|String
name|getOutputName
parameter_list|(
name|int
name|partition
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

