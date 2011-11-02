begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.jobhistory
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|jobhistory
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|net
operator|.
name|InetAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetSocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|UnknownHostException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Calendar
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|FileContext
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
name|PathFilter
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
name|RemoteIterator
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
name|permission
operator|.
name|FsPermission
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
name|JobID
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
name|MRJobConfig
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
name|TypeConverter
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
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|JobId
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
name|v2
operator|.
name|util
operator|.
name|MRApps
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
name|net
operator|.
name|NetUtils
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
name|UserGroupInformation
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ApplicationId
import|;
end_import

begin_class
DECL|class|JobHistoryUtils
specifier|public
class|class
name|JobHistoryUtils
block|{
comment|/**    * Permissions for the history staging dir while JobInProgress.    */
DECL|field|HISTORY_STAGING_DIR_PERMISSIONS
specifier|public
specifier|static
specifier|final
name|FsPermission
name|HISTORY_STAGING_DIR_PERMISSIONS
init|=
name|FsPermission
operator|.
name|createImmutable
argument_list|(
operator|(
name|short
operator|)
literal|0700
argument_list|)
decl_stmt|;
comment|/**    * Permissions for the user directory under the staging directory.    */
DECL|field|HISTORY_STAGING_USER_DIR_PERMISSIONS
specifier|public
specifier|static
specifier|final
name|FsPermission
name|HISTORY_STAGING_USER_DIR_PERMISSIONS
init|=
name|FsPermission
operator|.
name|createImmutable
argument_list|(
operator|(
name|short
operator|)
literal|0700
argument_list|)
decl_stmt|;
comment|/**    * Permissions for the history done dir and derivatives.    */
DECL|field|HISTORY_DONE_DIR_PERMISSION
specifier|public
specifier|static
specifier|final
name|FsPermission
name|HISTORY_DONE_DIR_PERMISSION
init|=
name|FsPermission
operator|.
name|createImmutable
argument_list|(
operator|(
name|short
operator|)
literal|0770
argument_list|)
decl_stmt|;
DECL|field|HISTORY_DONE_FILE_PERMISSION
specifier|public
specifier|static
specifier|final
name|FsPermission
name|HISTORY_DONE_FILE_PERMISSION
init|=
name|FsPermission
operator|.
name|createImmutable
argument_list|(
operator|(
name|short
operator|)
literal|0770
argument_list|)
decl_stmt|;
comment|// rwx------
comment|/**    * Permissions for the intermediate done directory.    */
DECL|field|HISTORY_INTERMEDIATE_DONE_DIR_PERMISSIONS
specifier|public
specifier|static
specifier|final
name|FsPermission
name|HISTORY_INTERMEDIATE_DONE_DIR_PERMISSIONS
init|=
name|FsPermission
operator|.
name|createImmutable
argument_list|(
operator|(
name|short
operator|)
literal|01777
argument_list|)
decl_stmt|;
comment|/**    * Permissions for the user directory under the intermediate done directory.    */
DECL|field|HISTORY_INTERMEDIATE_USER_DIR_PERMISSIONS
specifier|public
specifier|static
specifier|final
name|FsPermission
name|HISTORY_INTERMEDIATE_USER_DIR_PERMISSIONS
init|=
name|FsPermission
operator|.
name|createImmutable
argument_list|(
operator|(
name|short
operator|)
literal|0770
argument_list|)
decl_stmt|;
DECL|field|HISTORY_INTERMEDIATE_FILE_PERMISSIONS
specifier|public
specifier|static
specifier|final
name|FsPermission
name|HISTORY_INTERMEDIATE_FILE_PERMISSIONS
init|=
name|FsPermission
operator|.
name|createImmutable
argument_list|(
operator|(
name|short
operator|)
literal|0770
argument_list|)
decl_stmt|;
comment|// rwx------
comment|/**    * Suffix for configuration files.    */
DECL|field|CONF_FILE_NAME_SUFFIX
specifier|public
specifier|static
specifier|final
name|String
name|CONF_FILE_NAME_SUFFIX
init|=
literal|"_conf.xml"
decl_stmt|;
comment|/**    * Suffix for summary files.    */
DECL|field|SUMMARY_FILE_NAME_SUFFIX
specifier|public
specifier|static
specifier|final
name|String
name|SUMMARY_FILE_NAME_SUFFIX
init|=
literal|".summary"
decl_stmt|;
comment|/**    * Job History File extension.    */
DECL|field|JOB_HISTORY_FILE_EXTENSION
specifier|public
specifier|static
specifier|final
name|String
name|JOB_HISTORY_FILE_EXTENSION
init|=
literal|".jhist"
decl_stmt|;
DECL|field|VERSION
specifier|public
specifier|static
specifier|final
name|int
name|VERSION
init|=
literal|4
decl_stmt|;
DECL|field|SERIAL_NUMBER_DIRECTORY_DIGITS
specifier|public
specifier|static
specifier|final
name|int
name|SERIAL_NUMBER_DIRECTORY_DIGITS
init|=
literal|6
decl_stmt|;
DECL|field|TIMESTAMP_DIR_REGEX
specifier|public
specifier|static
specifier|final
name|String
name|TIMESTAMP_DIR_REGEX
init|=
literal|"\\d{4}"
operator|+
literal|"\\"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"\\d{2}"
operator|+
literal|"\\"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"\\d{2}"
decl_stmt|;
DECL|field|TIMESTAMP_DIR_PATTERN
specifier|public
specifier|static
specifier|final
name|Pattern
name|TIMESTAMP_DIR_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
name|TIMESTAMP_DIR_REGEX
argument_list|)
decl_stmt|;
DECL|field|TIMESTAMP_DIR_FORMAT
specifier|private
specifier|static
specifier|final
name|String
name|TIMESTAMP_DIR_FORMAT
init|=
literal|"%04d"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"%02d"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"%02d"
decl_stmt|;
DECL|field|CONF_FILTER
specifier|private
specifier|static
specifier|final
name|PathFilter
name|CONF_FILTER
init|=
operator|new
name|PathFilter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|accept
parameter_list|(
name|Path
name|path
parameter_list|)
block|{
return|return
name|path
operator|.
name|getName
argument_list|()
operator|.
name|endsWith
argument_list|(
name|CONF_FILE_NAME_SUFFIX
argument_list|)
return|;
block|}
block|}
decl_stmt|;
DECL|field|JOB_HISTORY_FILE_FILTER
specifier|private
specifier|static
specifier|final
name|PathFilter
name|JOB_HISTORY_FILE_FILTER
init|=
operator|new
name|PathFilter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|accept
parameter_list|(
name|Path
name|path
parameter_list|)
block|{
return|return
name|path
operator|.
name|getName
argument_list|()
operator|.
name|endsWith
argument_list|(
name|JOB_HISTORY_FILE_EXTENSION
argument_list|)
return|;
block|}
block|}
decl_stmt|;
comment|/**    * Checks whether the provided path string is a valid job history file.    * @param pathString the path to be checked.    * @return true is the path is a valid job history filename else return false    */
DECL|method|isValidJobHistoryFileName (String pathString)
specifier|public
specifier|static
name|boolean
name|isValidJobHistoryFileName
parameter_list|(
name|String
name|pathString
parameter_list|)
block|{
return|return
name|pathString
operator|.
name|endsWith
argument_list|(
name|JOB_HISTORY_FILE_EXTENSION
argument_list|)
return|;
block|}
comment|/**    * Returns the jobId from a job history file name.    * @param pathString the path string.    * @return the JobId    * @throws IOException if the filename format is invalid.    */
DECL|method|getJobIDFromHistoryFilePath (String pathString)
specifier|public
specifier|static
name|JobID
name|getJobIDFromHistoryFilePath
parameter_list|(
name|String
name|pathString
parameter_list|)
throws|throws
name|IOException
block|{
name|String
index|[]
name|parts
init|=
name|pathString
operator|.
name|split
argument_list|(
name|Path
operator|.
name|SEPARATOR
argument_list|)
decl_stmt|;
name|String
name|fileNamePart
init|=
name|parts
index|[
name|parts
operator|.
name|length
operator|-
literal|1
index|]
decl_stmt|;
name|JobIndexInfo
name|jobIndexInfo
init|=
name|FileNameIndexUtils
operator|.
name|getIndexInfo
argument_list|(
name|fileNamePart
argument_list|)
decl_stmt|;
return|return
name|TypeConverter
operator|.
name|fromYarn
argument_list|(
name|jobIndexInfo
operator|.
name|getJobId
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Gets a PathFilter which would match configuration files.    * @return the patch filter {@link PathFilter} for matching conf files.    */
DECL|method|getConfFileFilter ()
specifier|public
specifier|static
name|PathFilter
name|getConfFileFilter
parameter_list|()
block|{
return|return
name|CONF_FILTER
return|;
block|}
comment|/**    * Gets a PathFilter which would match job history file names.    * @return the path filter {@link PathFilter} matching job history files.    */
DECL|method|getHistoryFileFilter ()
specifier|public
specifier|static
name|PathFilter
name|getHistoryFileFilter
parameter_list|()
block|{
return|return
name|JOB_HISTORY_FILE_FILTER
return|;
block|}
comment|/**    * Gets the configured directory prefix for In Progress history files.    * @param conf    * @return A string representation of the prefix.    */
specifier|public
specifier|static
name|String
DECL|method|getConfiguredHistoryStagingDirPrefix (Configuration conf)
name|getConfiguredHistoryStagingDirPrefix
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|user
init|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getShortUserName
argument_list|()
decl_stmt|;
name|Path
name|path
init|=
name|MRApps
operator|.
name|getStagingAreaDir
argument_list|(
name|conf
argument_list|,
name|user
argument_list|)
decl_stmt|;
name|String
name|logDir
init|=
name|path
operator|.
name|toString
argument_list|()
decl_stmt|;
return|return
name|logDir
return|;
block|}
comment|/**    * Gets the configured directory prefix for intermediate done history files.    * @param conf    * @return A string representation of the prefix.    */
DECL|method|getConfiguredHistoryIntermediateDoneDirPrefix ( Configuration conf)
specifier|public
specifier|static
name|String
name|getConfiguredHistoryIntermediateDoneDirPrefix
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|String
name|doneDirPrefix
init|=
name|conf
operator|.
name|get
argument_list|(
name|JHAdminConfig
operator|.
name|MR_HISTORY_INTERMEDIATE_DONE_DIR
argument_list|)
decl_stmt|;
if|if
condition|(
name|doneDirPrefix
operator|==
literal|null
condition|)
block|{
name|doneDirPrefix
operator|=
name|conf
operator|.
name|get
argument_list|(
name|MRJobConfig
operator|.
name|MR_AM_STAGING_DIR
argument_list|)
operator|+
literal|"/history/done_intermediate"
expr_stmt|;
block|}
return|return
name|doneDirPrefix
return|;
block|}
comment|/**    * Gets the configured directory prefix for Done history files.    * @param conf the configuration object    * @return the done history directory    */
DECL|method|getConfiguredHistoryServerDoneDirPrefix ( Configuration conf)
specifier|public
specifier|static
name|String
name|getConfiguredHistoryServerDoneDirPrefix
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|String
name|doneDirPrefix
init|=
name|conf
operator|.
name|get
argument_list|(
name|JHAdminConfig
operator|.
name|MR_HISTORY_DONE_DIR
argument_list|)
decl_stmt|;
if|if
condition|(
name|doneDirPrefix
operator|==
literal|null
condition|)
block|{
name|doneDirPrefix
operator|=
name|conf
operator|.
name|get
argument_list|(
name|MRJobConfig
operator|.
name|MR_AM_STAGING_DIR
argument_list|)
operator|+
literal|"/history/done"
expr_stmt|;
block|}
return|return
name|doneDirPrefix
return|;
block|}
comment|/**    * Gets the user directory for intermediate done history files.    * @param conf the configuration object    * @return the intermediate done directory for jobhistory files.    */
DECL|method|getHistoryIntermediateDoneDirForUser (Configuration conf)
specifier|public
specifier|static
name|String
name|getHistoryIntermediateDoneDirForUser
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getConfiguredHistoryIntermediateDoneDirPrefix
argument_list|(
name|conf
argument_list|)
operator|+
name|File
operator|.
name|separator
operator|+
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getShortUserName
argument_list|()
return|;
block|}
DECL|method|shouldCreateNonUserDirectory (Configuration conf)
specifier|public
specifier|static
name|boolean
name|shouldCreateNonUserDirectory
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
comment|// Returning true by default to allow non secure single node clusters to work
comment|// without any configuration change.
return|return
name|conf
operator|.
name|getBoolean
argument_list|(
name|MRJobConfig
operator|.
name|MR_AM_CREATE_JH_INTERMEDIATE_BASE_DIR
argument_list|,
literal|true
argument_list|)
return|;
block|}
comment|/**    * Get the job history file path for non Done history files.    */
DECL|method|getStagingJobHistoryFile (Path dir, JobId jobId, int attempt)
specifier|public
specifier|static
name|Path
name|getStagingJobHistoryFile
parameter_list|(
name|Path
name|dir
parameter_list|,
name|JobId
name|jobId
parameter_list|,
name|int
name|attempt
parameter_list|)
block|{
return|return
name|getStagingJobHistoryFile
argument_list|(
name|dir
argument_list|,
name|TypeConverter
operator|.
name|fromYarn
argument_list|(
name|jobId
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|attempt
argument_list|)
return|;
block|}
comment|/**    * Get the job history file path for non Done history files.    */
DECL|method|getStagingJobHistoryFile (Path dir, String jobId, int attempt)
specifier|public
specifier|static
name|Path
name|getStagingJobHistoryFile
parameter_list|(
name|Path
name|dir
parameter_list|,
name|String
name|jobId
parameter_list|,
name|int
name|attempt
parameter_list|)
block|{
return|return
operator|new
name|Path
argument_list|(
name|dir
argument_list|,
name|jobId
operator|+
literal|"_"
operator|+
name|attempt
operator|+
name|JOB_HISTORY_FILE_EXTENSION
argument_list|)
return|;
block|}
comment|/**    * Get the done configuration file name for a job.    * @param jobId the jobId.    * @return the conf file name.    */
DECL|method|getIntermediateConfFileName (JobId jobId)
specifier|public
specifier|static
name|String
name|getIntermediateConfFileName
parameter_list|(
name|JobId
name|jobId
parameter_list|)
block|{
return|return
name|TypeConverter
operator|.
name|fromYarn
argument_list|(
name|jobId
argument_list|)
operator|.
name|toString
argument_list|()
operator|+
name|CONF_FILE_NAME_SUFFIX
return|;
block|}
comment|/**    * Get the done summary file name for a job.    * @param jobId the jobId.    * @return the conf file name.    */
DECL|method|getIntermediateSummaryFileName (JobId jobId)
specifier|public
specifier|static
name|String
name|getIntermediateSummaryFileName
parameter_list|(
name|JobId
name|jobId
parameter_list|)
block|{
return|return
name|TypeConverter
operator|.
name|fromYarn
argument_list|(
name|jobId
argument_list|)
operator|.
name|toString
argument_list|()
operator|+
name|SUMMARY_FILE_NAME_SUFFIX
return|;
block|}
comment|/**    * Gets the conf file path for jobs in progress.    *     * @param logDir the log directory prefix.    * @param jobId the jobId.    * @param attempt attempt number for this job.    * @return the conf file path for jobs in progress.    */
DECL|method|getStagingConfFile (Path logDir, JobId jobId, int attempt)
specifier|public
specifier|static
name|Path
name|getStagingConfFile
parameter_list|(
name|Path
name|logDir
parameter_list|,
name|JobId
name|jobId
parameter_list|,
name|int
name|attempt
parameter_list|)
block|{
name|Path
name|jobFilePath
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|logDir
operator|!=
literal|null
condition|)
block|{
name|jobFilePath
operator|=
operator|new
name|Path
argument_list|(
name|logDir
argument_list|,
name|TypeConverter
operator|.
name|fromYarn
argument_list|(
name|jobId
argument_list|)
operator|.
name|toString
argument_list|()
operator|+
literal|"_"
operator|+
name|attempt
operator|+
name|CONF_FILE_NAME_SUFFIX
argument_list|)
expr_stmt|;
block|}
return|return
name|jobFilePath
return|;
block|}
comment|/**    * Gets the serial number part of the path based on the jobId and serialNumber format.    * @param id    * @param serialNumberFormat    * @return the serial number part of the patch based on the jobId and serial number format.    */
DECL|method|serialNumberDirectoryComponent (JobId id, String serialNumberFormat)
specifier|public
specifier|static
name|String
name|serialNumberDirectoryComponent
parameter_list|(
name|JobId
name|id
parameter_list|,
name|String
name|serialNumberFormat
parameter_list|)
block|{
return|return
name|String
operator|.
name|format
argument_list|(
name|serialNumberFormat
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
name|jobSerialNumber
argument_list|(
name|id
argument_list|)
argument_list|)
argument_list|)
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|SERIAL_NUMBER_DIRECTORY_DIGITS
argument_list|)
return|;
block|}
comment|/**Extracts the timstamp component from the path.    * @param path    * @return the timestamp component from the path    */
DECL|method|getTimestampPartFromPath (String path)
specifier|public
specifier|static
name|String
name|getTimestampPartFromPath
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|Matcher
name|matcher
init|=
name|TIMESTAMP_DIR_PATTERN
operator|.
name|matcher
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|matcher
operator|.
name|find
argument_list|()
condition|)
block|{
name|String
name|matched
init|=
name|matcher
operator|.
name|group
argument_list|()
decl_stmt|;
name|String
name|ret
init|=
name|matched
operator|.
name|intern
argument_list|()
decl_stmt|;
return|return
name|ret
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
comment|/**    * Gets the history subdirectory based on the jobId, timestamp and serial number format.    * @param id    * @param timestampComponent    * @param serialNumberFormat    * @return the history sub directory based on the jobid, timestamp and serial number format    */
DECL|method|historyLogSubdirectory (JobId id, String timestampComponent, String serialNumberFormat)
specifier|public
specifier|static
name|String
name|historyLogSubdirectory
parameter_list|(
name|JobId
name|id
parameter_list|,
name|String
name|timestampComponent
parameter_list|,
name|String
name|serialNumberFormat
parameter_list|)
block|{
comment|//    String result = LOG_VERSION_STRING;
name|String
name|result
init|=
literal|""
decl_stmt|;
name|String
name|serialNumberDirectory
init|=
name|serialNumberDirectoryComponent
argument_list|(
name|id
argument_list|,
name|serialNumberFormat
argument_list|)
decl_stmt|;
name|result
operator|=
name|result
operator|+
name|timestampComponent
operator|+
name|File
operator|.
name|separator
operator|+
name|serialNumberDirectory
operator|+
name|File
operator|.
name|separator
expr_stmt|;
return|return
name|result
return|;
block|}
comment|/**    * Gets the timestamp component based on millisecond time.    * @param millisecondTime    * @param debugMode    * @return the timestamp component based on millisecond time    */
DECL|method|timestampDirectoryComponent (long millisecondTime, boolean debugMode)
specifier|public
specifier|static
name|String
name|timestampDirectoryComponent
parameter_list|(
name|long
name|millisecondTime
parameter_list|,
name|boolean
name|debugMode
parameter_list|)
block|{
name|Calendar
name|timestamp
init|=
name|Calendar
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|timestamp
operator|.
name|setTimeInMillis
argument_list|(
name|millisecondTime
argument_list|)
expr_stmt|;
name|String
name|dateString
init|=
literal|null
decl_stmt|;
name|dateString
operator|=
name|String
operator|.
name|format
argument_list|(
name|TIMESTAMP_DIR_FORMAT
argument_list|,
name|timestamp
operator|.
name|get
argument_list|(
name|Calendar
operator|.
name|YEAR
argument_list|)
argument_list|,
comment|// months are 0-based in Calendar, but people will expect January
comment|// to be month #1.
name|timestamp
operator|.
name|get
argument_list|(
name|debugMode
condition|?
name|Calendar
operator|.
name|HOUR
else|:
name|Calendar
operator|.
name|MONTH
argument_list|)
operator|+
literal|1
argument_list|,
name|timestamp
operator|.
name|get
argument_list|(
name|debugMode
condition|?
name|Calendar
operator|.
name|MINUTE
else|:
name|Calendar
operator|.
name|DAY_OF_MONTH
argument_list|)
argument_list|)
expr_stmt|;
name|dateString
operator|=
name|dateString
operator|.
name|intern
argument_list|()
expr_stmt|;
return|return
name|dateString
return|;
block|}
DECL|method|doneSubdirsBeforeSerialTail ()
specifier|public
specifier|static
name|String
name|doneSubdirsBeforeSerialTail
parameter_list|()
block|{
comment|// date
name|String
name|result
init|=
literal|"/*/*/*"
decl_stmt|;
comment|// YYYY/MM/DD ;
return|return
name|result
return|;
block|}
comment|/**    * Computes a serial number used as part of directory naming for the given jobId.    * @param id the jobId.    * @return the serial number used as part of directory naming for the given jobid    */
DECL|method|jobSerialNumber (JobId id)
specifier|public
specifier|static
name|int
name|jobSerialNumber
parameter_list|(
name|JobId
name|id
parameter_list|)
block|{
return|return
name|id
operator|.
name|getId
argument_list|()
return|;
block|}
DECL|method|localGlobber (FileContext fc, Path root, String tail)
specifier|public
specifier|static
name|List
argument_list|<
name|FileStatus
argument_list|>
name|localGlobber
parameter_list|(
name|FileContext
name|fc
parameter_list|,
name|Path
name|root
parameter_list|,
name|String
name|tail
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|localGlobber
argument_list|(
name|fc
argument_list|,
name|root
argument_list|,
name|tail
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|localGlobber (FileContext fc, Path root, String tail, PathFilter filter)
specifier|public
specifier|static
name|List
argument_list|<
name|FileStatus
argument_list|>
name|localGlobber
parameter_list|(
name|FileContext
name|fc
parameter_list|,
name|Path
name|root
parameter_list|,
name|String
name|tail
parameter_list|,
name|PathFilter
name|filter
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|localGlobber
argument_list|(
name|fc
argument_list|,
name|root
argument_list|,
name|tail
argument_list|,
name|filter
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|// hasMismatches is just used to return a second value if you want
comment|// one. I would have used MutableBoxedBoolean if such had been provided.
DECL|method|localGlobber (FileContext fc, Path root, String tail, PathFilter filter, AtomicBoolean hasFlatFiles)
specifier|public
specifier|static
name|List
argument_list|<
name|FileStatus
argument_list|>
name|localGlobber
parameter_list|(
name|FileContext
name|fc
parameter_list|,
name|Path
name|root
parameter_list|,
name|String
name|tail
parameter_list|,
name|PathFilter
name|filter
parameter_list|,
name|AtomicBoolean
name|hasFlatFiles
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|tail
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
condition|)
block|{
return|return
operator|(
name|listFilteredStatus
argument_list|(
name|fc
argument_list|,
name|root
argument_list|,
name|filter
argument_list|)
operator|)
return|;
block|}
if|if
condition|(
name|tail
operator|.
name|startsWith
argument_list|(
literal|"/*"
argument_list|)
condition|)
block|{
name|Path
index|[]
name|subdirs
init|=
name|filteredStat2Paths
argument_list|(
name|remoteIterToList
argument_list|(
name|fc
operator|.
name|listStatus
argument_list|(
name|root
argument_list|)
argument_list|)
argument_list|,
literal|true
argument_list|,
name|hasFlatFiles
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|List
argument_list|<
name|FileStatus
argument_list|>
argument_list|>
name|subsubdirs
init|=
operator|new
name|LinkedList
argument_list|<
name|List
argument_list|<
name|FileStatus
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|subsubdirCount
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|subdirs
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
operator|new
name|LinkedList
argument_list|<
name|FileStatus
argument_list|>
argument_list|()
return|;
block|}
name|String
name|newTail
init|=
name|tail
operator|.
name|substring
argument_list|(
literal|2
argument_list|)
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
name|subdirs
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|subsubdirs
operator|.
name|add
argument_list|(
name|localGlobber
argument_list|(
name|fc
argument_list|,
name|subdirs
index|[
name|i
index|]
argument_list|,
name|newTail
argument_list|,
name|filter
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
comment|// subsubdirs.set(i, localGlobber(fc, subdirs[i], newTail, filter,
comment|// null));
name|subsubdirCount
operator|+=
name|subsubdirs
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
name|List
argument_list|<
name|FileStatus
argument_list|>
name|result
init|=
operator|new
name|LinkedList
argument_list|<
name|FileStatus
argument_list|>
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
name|subsubdirs
operator|.
name|size
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
name|result
operator|.
name|addAll
argument_list|(
name|subsubdirs
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
if|if
condition|(
name|tail
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|int
name|split
init|=
name|tail
operator|.
name|indexOf
argument_list|(
literal|'/'
argument_list|,
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|split
operator|<
literal|0
condition|)
block|{
return|return
name|listFilteredStatus
argument_list|(
name|fc
argument_list|,
operator|new
name|Path
argument_list|(
name|root
argument_list|,
name|tail
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|,
name|filter
argument_list|)
return|;
block|}
else|else
block|{
name|String
name|thisSegment
init|=
name|tail
operator|.
name|substring
argument_list|(
literal|1
argument_list|,
name|split
argument_list|)
decl_stmt|;
name|String
name|newTail
init|=
name|tail
operator|.
name|substring
argument_list|(
name|split
argument_list|)
decl_stmt|;
return|return
name|localGlobber
argument_list|(
name|fc
argument_list|,
operator|new
name|Path
argument_list|(
name|root
argument_list|,
name|thisSegment
argument_list|)
argument_list|,
name|newTail
argument_list|,
name|filter
argument_list|,
name|hasFlatFiles
argument_list|)
return|;
block|}
block|}
name|IOException
name|e
init|=
operator|new
name|IOException
argument_list|(
literal|"localGlobber: bad tail"
argument_list|)
decl_stmt|;
throw|throw
name|e
throw|;
block|}
DECL|method|listFilteredStatus (FileContext fc, Path root, PathFilter filter)
specifier|private
specifier|static
name|List
argument_list|<
name|FileStatus
argument_list|>
name|listFilteredStatus
parameter_list|(
name|FileContext
name|fc
parameter_list|,
name|Path
name|root
parameter_list|,
name|PathFilter
name|filter
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|FileStatus
argument_list|>
name|fsList
init|=
name|remoteIterToList
argument_list|(
name|fc
operator|.
name|listStatus
argument_list|(
name|root
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|filter
operator|==
literal|null
condition|)
block|{
return|return
name|fsList
return|;
block|}
else|else
block|{
name|List
argument_list|<
name|FileStatus
argument_list|>
name|filteredList
init|=
operator|new
name|LinkedList
argument_list|<
name|FileStatus
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|FileStatus
name|fs
range|:
name|fsList
control|)
block|{
if|if
condition|(
name|filter
operator|.
name|accept
argument_list|(
name|fs
operator|.
name|getPath
argument_list|()
argument_list|)
condition|)
block|{
name|filteredList
operator|.
name|add
argument_list|(
name|fs
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|filteredList
return|;
block|}
block|}
DECL|method|remoteIterToList ( RemoteIterator<FileStatus> rIter)
specifier|private
specifier|static
name|List
argument_list|<
name|FileStatus
argument_list|>
name|remoteIterToList
parameter_list|(
name|RemoteIterator
argument_list|<
name|FileStatus
argument_list|>
name|rIter
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|FileStatus
argument_list|>
name|fsList
init|=
operator|new
name|LinkedList
argument_list|<
name|FileStatus
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|rIter
operator|==
literal|null
condition|)
return|return
name|fsList
return|;
while|while
condition|(
name|rIter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|fsList
operator|.
name|add
argument_list|(
name|rIter
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|fsList
return|;
block|}
comment|// hasMismatches is just used to return a second value if you want
comment|// one. I would have used MutableBoxedBoolean if such had been provided.
DECL|method|filteredStat2Paths (List<FileStatus> stats, boolean dirs, AtomicBoolean hasMismatches)
specifier|private
specifier|static
name|Path
index|[]
name|filteredStat2Paths
parameter_list|(
name|List
argument_list|<
name|FileStatus
argument_list|>
name|stats
parameter_list|,
name|boolean
name|dirs
parameter_list|,
name|AtomicBoolean
name|hasMismatches
parameter_list|)
block|{
name|int
name|resultCount
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|hasMismatches
operator|==
literal|null
condition|)
block|{
name|hasMismatches
operator|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|stats
operator|.
name|size
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
if|if
condition|(
name|stats
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|isDirectory
argument_list|()
operator|==
name|dirs
condition|)
block|{
name|stats
operator|.
name|set
argument_list|(
name|resultCount
operator|++
argument_list|,
name|stats
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|hasMismatches
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
name|Path
index|[]
name|result
init|=
operator|new
name|Path
index|[
name|resultCount
index|]
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
name|resultCount
condition|;
name|i
operator|++
control|)
block|{
name|result
index|[
name|i
index|]
operator|=
name|stats
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getPath
argument_list|()
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
DECL|method|getHistoryUrl (Configuration conf, ApplicationId appId)
specifier|public
specifier|static
name|String
name|getHistoryUrl
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|ApplicationId
name|appId
parameter_list|)
throws|throws
name|UnknownHostException
block|{
comment|//construct the history url for job
name|String
name|hsAddress
init|=
name|conf
operator|.
name|get
argument_list|(
name|JHAdminConfig
operator|.
name|MR_HISTORY_WEBAPP_ADDRESS
argument_list|,
name|JHAdminConfig
operator|.
name|DEFAULT_MR_HISTORY_WEBAPP_ADDRESS
argument_list|)
decl_stmt|;
name|InetSocketAddress
name|address
init|=
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|hsAddress
argument_list|,
name|JHAdminConfig
operator|.
name|DEFAULT_MR_HISTORY_WEBAPP_PORT
argument_list|,
name|JHAdminConfig
operator|.
name|DEFAULT_MR_HISTORY_WEBAPP_ADDRESS
argument_list|)
decl_stmt|;
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
if|if
condition|(
name|address
operator|.
name|getAddress
argument_list|()
operator|.
name|isAnyLocalAddress
argument_list|()
operator|||
name|address
operator|.
name|getAddress
argument_list|()
operator|.
name|isLoopbackAddress
argument_list|()
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|InetAddress
operator|.
name|getLocalHost
argument_list|()
operator|.
name|getHostAddress
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
name|address
operator|.
name|getHostName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|":"
argument_list|)
operator|.
name|append
argument_list|(
name|address
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"/jobhistory/job/"
argument_list|)
expr_stmt|;
comment|// TODO This will change when the history server
comment|// understands apps.
comment|// TOOD Use JobId toString once UI stops using _id_id
name|sb
operator|.
name|append
argument_list|(
literal|"job_"
argument_list|)
operator|.
name|append
argument_list|(
name|appId
operator|.
name|getClusterTimestamp
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"_"
argument_list|)
operator|.
name|append
argument_list|(
name|appId
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"_"
argument_list|)
operator|.
name|append
argument_list|(
name|appId
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
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

