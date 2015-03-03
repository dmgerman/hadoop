begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
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
name|net
operator|.
name|URI
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
name|Configuration
operator|.
name|IntegerRanges
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
name|io
operator|.
name|RawComparator
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
name|Mapper
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
name|security
operator|.
name|Credentials
import|;
end_import

begin_comment
comment|/**  * A read-only view of the job that is provided to the tasks while they  * are running.  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|interface|JobContext
specifier|public
interface|interface
name|JobContext
extends|extends
name|MRJobConfig
block|{
comment|/**    * Return the configuration for the job.    * @return the shared configuration object    */
DECL|method|getConfiguration ()
specifier|public
name|Configuration
name|getConfiguration
parameter_list|()
function_decl|;
comment|/**    * Get credentials for the job.    * @return credentials for the job    */
DECL|method|getCredentials ()
specifier|public
name|Credentials
name|getCredentials
parameter_list|()
function_decl|;
comment|/**    * Get the unique ID for the job.    * @return the object with the job id    */
DECL|method|getJobID ()
specifier|public
name|JobID
name|getJobID
parameter_list|()
function_decl|;
comment|/**    * Get configured the number of reduce tasks for this job. Defaults to     *<code>1</code>.    * @return the number of reduce tasks for this job.    */
DECL|method|getNumReduceTasks ()
specifier|public
name|int
name|getNumReduceTasks
parameter_list|()
function_decl|;
comment|/**    * Get the current working directory for the default file system.    *     * @return the directory name.    */
DECL|method|getWorkingDirectory ()
specifier|public
name|Path
name|getWorkingDirectory
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Get the key class for the job output data.    * @return the key class for the job output data.    */
DECL|method|getOutputKeyClass ()
specifier|public
name|Class
argument_list|<
name|?
argument_list|>
name|getOutputKeyClass
parameter_list|()
function_decl|;
comment|/**    * Get the value class for job outputs.    * @return the value class for job outputs.    */
DECL|method|getOutputValueClass ()
specifier|public
name|Class
argument_list|<
name|?
argument_list|>
name|getOutputValueClass
parameter_list|()
function_decl|;
comment|/**    * Get the key class for the map output data. If it is not set, use the    * (final) output key class. This allows the map output key class to be    * different than the final output key class.    * @return the map output key class.    */
DECL|method|getMapOutputKeyClass ()
specifier|public
name|Class
argument_list|<
name|?
argument_list|>
name|getMapOutputKeyClass
parameter_list|()
function_decl|;
comment|/**    * Get the value class for the map output data. If it is not set, use the    * (final) output value class This allows the map output value class to be    * different than the final output value class.    *      * @return the map output value class.    */
DECL|method|getMapOutputValueClass ()
specifier|public
name|Class
argument_list|<
name|?
argument_list|>
name|getMapOutputValueClass
parameter_list|()
function_decl|;
comment|/**    * Get the user-specified job name. This is only used to identify the     * job to the user.    *     * @return the job's name, defaulting to "".    */
DECL|method|getJobName ()
specifier|public
name|String
name|getJobName
parameter_list|()
function_decl|;
comment|/**    * Get the {@link InputFormat} class for the job.    *     * @return the {@link InputFormat} class for the job.    */
DECL|method|getInputFormatClass ()
specifier|public
name|Class
argument_list|<
name|?
extends|extends
name|InputFormat
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
argument_list|>
name|getInputFormatClass
parameter_list|()
throws|throws
name|ClassNotFoundException
function_decl|;
comment|/**    * Get the {@link Mapper} class for the job.    *     * @return the {@link Mapper} class for the job.    */
DECL|method|getMapperClass ()
specifier|public
name|Class
argument_list|<
name|?
extends|extends
name|Mapper
argument_list|<
name|?
argument_list|,
name|?
argument_list|,
name|?
argument_list|,
name|?
argument_list|>
argument_list|>
name|getMapperClass
parameter_list|()
throws|throws
name|ClassNotFoundException
function_decl|;
comment|/**    * Get the combiner class for the job.    *     * @return the combiner class for the job.    */
DECL|method|getCombinerClass ()
specifier|public
name|Class
argument_list|<
name|?
extends|extends
name|Reducer
argument_list|<
name|?
argument_list|,
name|?
argument_list|,
name|?
argument_list|,
name|?
argument_list|>
argument_list|>
name|getCombinerClass
parameter_list|()
throws|throws
name|ClassNotFoundException
function_decl|;
comment|/**    * Get the {@link Reducer} class for the job.    *     * @return the {@link Reducer} class for the job.    */
DECL|method|getReducerClass ()
specifier|public
name|Class
argument_list|<
name|?
extends|extends
name|Reducer
argument_list|<
name|?
argument_list|,
name|?
argument_list|,
name|?
argument_list|,
name|?
argument_list|>
argument_list|>
name|getReducerClass
parameter_list|()
throws|throws
name|ClassNotFoundException
function_decl|;
comment|/**    * Get the {@link OutputFormat} class for the job.    *     * @return the {@link OutputFormat} class for the job.    */
DECL|method|getOutputFormatClass ()
specifier|public
name|Class
argument_list|<
name|?
extends|extends
name|OutputFormat
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
argument_list|>
name|getOutputFormatClass
parameter_list|()
throws|throws
name|ClassNotFoundException
function_decl|;
comment|/**    * Get the {@link Partitioner} class for the job.    *     * @return the {@link Partitioner} class for the job.    */
DECL|method|getPartitionerClass ()
specifier|public
name|Class
argument_list|<
name|?
extends|extends
name|Partitioner
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
argument_list|>
name|getPartitionerClass
parameter_list|()
throws|throws
name|ClassNotFoundException
function_decl|;
comment|/**    * Get the {@link RawComparator} comparator used to compare keys.    *     * @return the {@link RawComparator} comparator used to compare keys.    */
DECL|method|getSortComparator ()
specifier|public
name|RawComparator
argument_list|<
name|?
argument_list|>
name|getSortComparator
parameter_list|()
function_decl|;
comment|/**    * Get the pathname of the job's jar.    * @return the pathname    */
DECL|method|getJar ()
specifier|public
name|String
name|getJar
parameter_list|()
function_decl|;
comment|/**    * Get the user defined {@link RawComparator} comparator for    * grouping keys of inputs to the combiner.    *    * @return comparator set by the user for grouping values.    * @see Job#setCombinerKeyGroupingComparatorClass(Class)    */
DECL|method|getCombinerKeyGroupingComparator ()
specifier|public
name|RawComparator
argument_list|<
name|?
argument_list|>
name|getCombinerKeyGroupingComparator
parameter_list|()
function_decl|;
comment|/**      * Get the user defined {@link RawComparator} comparator for      * grouping keys of inputs to the reduce.      *      * @return comparator set by the user for grouping values.      * @see Job#setGroupingComparatorClass(Class)      * @see #getCombinerKeyGroupingComparator()      */
DECL|method|getGroupingComparator ()
specifier|public
name|RawComparator
argument_list|<
name|?
argument_list|>
name|getGroupingComparator
parameter_list|()
function_decl|;
comment|/**    * Get whether job-setup and job-cleanup is needed for the job     *     * @return boolean     */
DECL|method|getJobSetupCleanupNeeded ()
specifier|public
name|boolean
name|getJobSetupCleanupNeeded
parameter_list|()
function_decl|;
comment|/**    * Get whether task-cleanup is needed for the job     *     * @return boolean     */
DECL|method|getTaskCleanupNeeded ()
specifier|public
name|boolean
name|getTaskCleanupNeeded
parameter_list|()
function_decl|;
comment|/**    * Get whether the task profiling is enabled.    * @return true if some tasks will be profiled    */
DECL|method|getProfileEnabled ()
specifier|public
name|boolean
name|getProfileEnabled
parameter_list|()
function_decl|;
comment|/**    * Get the profiler configuration arguments.    *    * The default value for this property is    * "-agentlib:hprof=cpu=samples,heap=sites,force=n,thread=y,verbose=n,file=%s"    *     * @return the parameters to pass to the task child to configure profiling    */
DECL|method|getProfileParams ()
specifier|public
name|String
name|getProfileParams
parameter_list|()
function_decl|;
comment|/**    * Get the range of maps or reduces to profile.    * @param isMap is the task a map?    * @return the task ranges    */
DECL|method|getProfileTaskRange (boolean isMap)
specifier|public
name|IntegerRanges
name|getProfileTaskRange
parameter_list|(
name|boolean
name|isMap
parameter_list|)
function_decl|;
comment|/**    * Get the reported username for this job.    *     * @return the username    */
DECL|method|getUser ()
specifier|public
name|String
name|getUser
parameter_list|()
function_decl|;
comment|/**    * Originally intended to check if symlinks should be used, but currently    * symlinks cannot be disabled.    * @return true    */
annotation|@
name|Deprecated
DECL|method|getSymlink ()
specifier|public
name|boolean
name|getSymlink
parameter_list|()
function_decl|;
comment|/**    * Get the archive entries in classpath as an array of Path    */
DECL|method|getArchiveClassPaths ()
specifier|public
name|Path
index|[]
name|getArchiveClassPaths
parameter_list|()
function_decl|;
comment|/**    * Get cache archives set in the Configuration    * @return A URI array of the caches set in the Configuration    * @throws IOException    */
DECL|method|getCacheArchives ()
specifier|public
name|URI
index|[]
name|getCacheArchives
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Get cache files set in the Configuration    * @return A URI array of the files set in the Configuration    * @throws IOException    */
DECL|method|getCacheFiles ()
specifier|public
name|URI
index|[]
name|getCacheFiles
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Return the path array of the localized caches    * @return A path array of localized caches    * @throws IOException    * @deprecated the array returned only includes the items the were     * downloaded. There is no way to map this to what is returned by    * {@link #getCacheArchives()}.    */
annotation|@
name|Deprecated
DECL|method|getLocalCacheArchives ()
specifier|public
name|Path
index|[]
name|getLocalCacheArchives
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Return the path array of the localized files    * @return A path array of localized files    * @throws IOException    * @deprecated the array returned only includes the items the were     * downloaded. There is no way to map this to what is returned by    * {@link #getCacheFiles()}.    */
annotation|@
name|Deprecated
DECL|method|getLocalCacheFiles ()
specifier|public
name|Path
index|[]
name|getLocalCacheFiles
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Get the file entries in classpath as an array of Path    */
DECL|method|getFileClassPaths ()
specifier|public
name|Path
index|[]
name|getFileClassPaths
parameter_list|()
function_decl|;
comment|/**    * Get the timestamps of the archives.  Used by internal    * DistributedCache and MapReduce code.    * @return a string array of timestamps     */
DECL|method|getArchiveTimestamps ()
specifier|public
name|String
index|[]
name|getArchiveTimestamps
parameter_list|()
function_decl|;
comment|/**    * Get the timestamps of the files.  Used by internal    * DistributedCache and MapReduce code.    * @return a string array of timestamps     */
DECL|method|getFileTimestamps ()
specifier|public
name|String
index|[]
name|getFileTimestamps
parameter_list|()
function_decl|;
comment|/**     * Get the configured number of maximum attempts that will be made to run a    * map task, as specified by the<code>mapred.map.max.attempts</code>    * property. If this property is not already set, the default is 4 attempts.    *      * @return the max number of attempts per map task.    */
DECL|method|getMaxMapAttempts ()
specifier|public
name|int
name|getMaxMapAttempts
parameter_list|()
function_decl|;
comment|/**     * Get the configured number of maximum attempts  that will be made to run a    * reduce task, as specified by the<code>mapred.reduce.max.attempts</code>    * property. If this property is not already set, the default is 4 attempts.    *     * @return the max number of attempts per reduce task.    */
DECL|method|getMaxReduceAttempts ()
specifier|public
name|int
name|getMaxReduceAttempts
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

