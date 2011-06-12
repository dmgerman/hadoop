begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.tools.rumen
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|tools
operator|.
name|rumen
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
name|InputStream
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
comment|/**  * Producing {@link JobStory}s from job trace.  */
end_comment

begin_class
DECL|class|ZombieJobProducer
specifier|public
class|class
name|ZombieJobProducer
implements|implements
name|JobStoryProducer
block|{
DECL|field|reader
specifier|private
specifier|final
name|JobTraceReader
name|reader
decl_stmt|;
DECL|field|cluster
specifier|private
specifier|final
name|ZombieCluster
name|cluster
decl_stmt|;
DECL|field|hasRandomSeed
specifier|private
name|boolean
name|hasRandomSeed
init|=
literal|false
decl_stmt|;
DECL|field|randomSeed
specifier|private
name|long
name|randomSeed
init|=
literal|0
decl_stmt|;
DECL|method|ZombieJobProducer (JobTraceReader reader, ZombieCluster cluster, boolean hasRandomSeed, long randomSeed)
specifier|private
name|ZombieJobProducer
parameter_list|(
name|JobTraceReader
name|reader
parameter_list|,
name|ZombieCluster
name|cluster
parameter_list|,
name|boolean
name|hasRandomSeed
parameter_list|,
name|long
name|randomSeed
parameter_list|)
block|{
name|this
operator|.
name|reader
operator|=
name|reader
expr_stmt|;
name|this
operator|.
name|cluster
operator|=
name|cluster
expr_stmt|;
name|this
operator|.
name|hasRandomSeed
operator|=
name|hasRandomSeed
expr_stmt|;
name|this
operator|.
name|randomSeed
operator|=
operator|(
name|hasRandomSeed
operator|)
condition|?
name|randomSeed
else|:
name|System
operator|.
name|nanoTime
argument_list|()
expr_stmt|;
block|}
comment|/**    * Constructor    *     * @param path    *          Path to the JSON trace file, possibly compressed.    * @param cluster    *          The topology of the cluster that corresponds to the jobs in the    *          trace. The argument can be null if we do not have knowledge of the    *          cluster topology.    * @param conf    * @throws IOException    */
DECL|method|ZombieJobProducer (Path path, ZombieCluster cluster, Configuration conf)
specifier|public
name|ZombieJobProducer
parameter_list|(
name|Path
name|path
parameter_list|,
name|ZombieCluster
name|cluster
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
operator|new
name|JobTraceReader
argument_list|(
name|path
argument_list|,
name|conf
argument_list|)
argument_list|,
name|cluster
argument_list|,
literal|false
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructor    *     * @param path    *          Path to the JSON trace file, possibly compressed.    * @param cluster    *          The topology of the cluster that corresponds to the jobs in the    *          trace. The argument can be null if we do not have knowledge of the    *          cluster topology.    * @param conf    * @param randomSeed    *          use a deterministic seed.    * @throws IOException    */
DECL|method|ZombieJobProducer (Path path, ZombieCluster cluster, Configuration conf, long randomSeed)
specifier|public
name|ZombieJobProducer
parameter_list|(
name|Path
name|path
parameter_list|,
name|ZombieCluster
name|cluster
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|long
name|randomSeed
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
operator|new
name|JobTraceReader
argument_list|(
name|path
argument_list|,
name|conf
argument_list|)
argument_list|,
name|cluster
argument_list|,
literal|true
argument_list|,
name|randomSeed
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructor    *     * @param input    *          The input stream for the JSON trace.    * @param cluster    *          The topology of the cluster that corresponds to the jobs in the    *          trace. The argument can be null if we do not have knowledge of the    *          cluster topology.    * @throws IOException    */
DECL|method|ZombieJobProducer (InputStream input, ZombieCluster cluster)
specifier|public
name|ZombieJobProducer
parameter_list|(
name|InputStream
name|input
parameter_list|,
name|ZombieCluster
name|cluster
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
operator|new
name|JobTraceReader
argument_list|(
name|input
argument_list|)
argument_list|,
name|cluster
argument_list|,
literal|false
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructor    *     * @param input    *          The input stream for the JSON trace.    * @param cluster    *          The topology of the cluster that corresponds to the jobs in the    *          trace. The argument can be null if we do not have knowledge of the    *          cluster topology.    * @param randomSeed    *          use a deterministic seed.    * @throws IOException    */
DECL|method|ZombieJobProducer (InputStream input, ZombieCluster cluster, long randomSeed)
specifier|public
name|ZombieJobProducer
parameter_list|(
name|InputStream
name|input
parameter_list|,
name|ZombieCluster
name|cluster
parameter_list|,
name|long
name|randomSeed
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
operator|new
name|JobTraceReader
argument_list|(
name|input
argument_list|)
argument_list|,
name|cluster
argument_list|,
literal|true
argument_list|,
name|randomSeed
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getNextJob ()
specifier|public
name|ZombieJob
name|getNextJob
parameter_list|()
throws|throws
name|IOException
block|{
name|LoggedJob
name|job
init|=
name|reader
operator|.
name|getNext
argument_list|()
decl_stmt|;
if|if
condition|(
name|job
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
elseif|else
if|if
condition|(
name|hasRandomSeed
condition|)
block|{
name|long
name|subRandomSeed
init|=
name|RandomSeedGenerator
operator|.
name|getSeed
argument_list|(
literal|"forZombieJob"
operator|+
name|job
operator|.
name|getJobID
argument_list|()
argument_list|,
name|randomSeed
argument_list|)
decl_stmt|;
return|return
operator|new
name|ZombieJob
argument_list|(
name|job
argument_list|,
name|cluster
argument_list|,
name|subRandomSeed
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|ZombieJob
argument_list|(
name|job
argument_list|,
name|cluster
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

