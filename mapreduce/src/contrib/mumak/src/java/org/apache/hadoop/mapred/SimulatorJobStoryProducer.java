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
name|tools
operator|.
name|rumen
operator|.
name|JobStory
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
name|tools
operator|.
name|rumen
operator|.
name|JobStoryProducer
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
name|tools
operator|.
name|rumen
operator|.
name|Pre21JobHistoryConstants
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
name|tools
operator|.
name|rumen
operator|.
name|ZombieCluster
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
name|tools
operator|.
name|rumen
operator|.
name|ZombieJob
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
name|tools
operator|.
name|rumen
operator|.
name|ZombieJobProducer
import|;
end_import

begin_comment
comment|/**  * This class creates {@link JobStory} objects from trace file in rumen format.  * It is a proxy class over {@link ZombieJobProducer}, and adjusts the  * submission time to be aligned with simulation time.  */
end_comment

begin_class
DECL|class|SimulatorJobStoryProducer
specifier|public
class|class
name|SimulatorJobStoryProducer
implements|implements
name|JobStoryProducer
block|{
DECL|field|producer
specifier|private
specifier|final
name|ZombieJobProducer
name|producer
decl_stmt|;
DECL|field|firstJobStartTime
specifier|private
specifier|final
name|long
name|firstJobStartTime
decl_stmt|;
DECL|field|relativeTime
specifier|private
name|long
name|relativeTime
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|firstJob
specifier|private
name|boolean
name|firstJob
init|=
literal|true
decl_stmt|;
DECL|method|SimulatorJobStoryProducer (Path path, ZombieCluster cluster, long firstJobStartTime, Configuration conf)
specifier|public
name|SimulatorJobStoryProducer
parameter_list|(
name|Path
name|path
parameter_list|,
name|ZombieCluster
name|cluster
parameter_list|,
name|long
name|firstJobStartTime
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|path
argument_list|,
name|cluster
argument_list|,
name|firstJobStartTime
argument_list|,
name|conf
argument_list|,
name|System
operator|.
name|nanoTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|SimulatorJobStoryProducer (Path path, ZombieCluster cluster, long firstJobStartTime, Configuration conf, long seed)
specifier|public
name|SimulatorJobStoryProducer
parameter_list|(
name|Path
name|path
parameter_list|,
name|ZombieCluster
name|cluster
parameter_list|,
name|long
name|firstJobStartTime
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|long
name|seed
parameter_list|)
throws|throws
name|IOException
block|{
name|producer
operator|=
operator|new
name|ZombieJobProducer
argument_list|(
name|path
argument_list|,
name|cluster
argument_list|,
name|conf
argument_list|,
name|seed
argument_list|)
expr_stmt|;
name|this
operator|.
name|firstJobStartTime
operator|=
name|firstJobStartTime
expr_stmt|;
block|}
comment|/**    * Filter some jobs being fed to the simulator. For now, we filter out killed    * jobs to facilitate debugging.    *     * @throws IOException    */
DECL|method|getNextJobFiltered ()
specifier|private
name|JobStory
name|getNextJobFiltered
parameter_list|()
throws|throws
name|IOException
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|ZombieJob
name|job
init|=
name|producer
operator|.
name|getNextJob
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
if|if
condition|(
name|job
operator|.
name|getOutcome
argument_list|()
operator|==
name|Pre21JobHistoryConstants
operator|.
name|Values
operator|.
name|KILLED
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|job
operator|.
name|getNumberMaps
argument_list|()
operator|==
literal|0
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|job
operator|.
name|getNumLoggedMaps
argument_list|()
operator|==
literal|0
condition|)
block|{
continue|continue;
block|}
return|return
name|job
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getNextJob ()
specifier|public
name|JobStory
name|getNextJob
parameter_list|()
throws|throws
name|IOException
block|{
name|JobStory
name|job
init|=
name|getNextJobFiltered
argument_list|()
decl_stmt|;
if|if
condition|(
name|job
operator|==
literal|null
condition|)
return|return
literal|null
return|;
if|if
condition|(
name|firstJob
condition|)
block|{
name|firstJob
operator|=
literal|false
expr_stmt|;
name|relativeTime
operator|=
name|job
operator|.
name|getSubmissionTime
argument_list|()
operator|-
name|firstJobStartTime
expr_stmt|;
block|}
return|return
operator|new
name|SimulatorJobStory
argument_list|(
name|job
argument_list|,
name|job
operator|.
name|getSubmissionTime
argument_list|()
operator|-
name|relativeTime
argument_list|)
return|;
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
name|producer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

