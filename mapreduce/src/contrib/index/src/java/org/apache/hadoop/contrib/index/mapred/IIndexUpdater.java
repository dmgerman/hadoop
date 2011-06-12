begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.contrib.index.mapred
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|contrib
operator|.
name|index
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

begin_comment
comment|/**  * A class implements an index updater interface should create a Map/Reduce job  * configuration and run the Map/Reduce job to analyze documents and update  * Lucene instances in parallel.  */
end_comment

begin_interface
DECL|interface|IIndexUpdater
specifier|public
interface|interface
name|IIndexUpdater
block|{
comment|/**    * Create a Map/Reduce job configuration and run the Map/Reduce job to    * analyze documents and update Lucene instances in parallel.    * @param conf    * @param inputPaths    * @param outputPath    * @param numMapTasks    * @param shards    * @throws IOException    */
DECL|method|run (Configuration conf, Path[] inputPaths, Path outputPath, int numMapTasks, Shard[] shards)
name|void
name|run
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|Path
index|[]
name|inputPaths
parameter_list|,
name|Path
name|outputPath
parameter_list|,
name|int
name|numMapTasks
parameter_list|,
name|Shard
index|[]
name|shards
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

