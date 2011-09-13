begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.eclipse.server
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|eclipse
operator|.
name|server
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
name|FileOutputStream
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
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|Map
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
name|java
operator|.
name|util
operator|.
name|TreeMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|DocumentBuilder
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|DocumentBuilderFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|ParserConfigurationException
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
name|eclipse
operator|.
name|Activator
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
name|io
operator|.
name|IOUtils
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
name|JobClient
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
name|JobConf
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
name|mapred
operator|.
name|JobStatus
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
name|RunningJob
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|core
operator|.
name|runtime
operator|.
name|IProgressMonitor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|core
operator|.
name|runtime
operator|.
name|IStatus
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|core
operator|.
name|runtime
operator|.
name|Status
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|core
operator|.
name|runtime
operator|.
name|jobs
operator|.
name|Job
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|swt
operator|.
name|widgets
operator|.
name|Display
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Document
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Element
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|NodeList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Text
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_comment
comment|/**  * Representation of a Hadoop location, meaning of the master node (NameNode,  * JobTracker).  *   *<p>  * This class does not create any SSH connection anymore. Tunneling must be  * setup outside of Eclipse for now (using Putty or<tt>ssh -D&lt;port&gt;  *&lt;host&gt;</tt>)  *   *<p>  *<em> TODO</em>  *<li> Disable the updater if a location becomes unreachable or fails for  * tool long  *<li> Stop the updater on location's disposal/removal  */
end_comment

begin_class
DECL|class|HadoopServer
specifier|public
class|class
name|HadoopServer
block|{
comment|/**    * Frequency of location status observations expressed as the delay in ms    * between each observation    *     * TODO Add a preference parameter for this    */
DECL|field|STATUS_OBSERVATION_DELAY
specifier|protected
specifier|static
specifier|final
name|long
name|STATUS_OBSERVATION_DELAY
init|=
literal|1500
decl_stmt|;
comment|/**    *     */
DECL|class|LocationStatusUpdater
specifier|public
class|class
name|LocationStatusUpdater
extends|extends
name|Job
block|{
DECL|field|client
name|JobClient
name|client
init|=
literal|null
decl_stmt|;
comment|/**      * Setup the updater      */
DECL|method|LocationStatusUpdater ()
specifier|public
name|LocationStatusUpdater
parameter_list|()
block|{
name|super
argument_list|(
literal|"Map/Reduce location status updater"
argument_list|)
expr_stmt|;
name|this
operator|.
name|setSystem
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/* @inheritDoc */
annotation|@
name|Override
DECL|method|run (IProgressMonitor monitor)
specifier|protected
name|IStatus
name|run
parameter_list|(
name|IProgressMonitor
name|monitor
parameter_list|)
block|{
if|if
condition|(
name|client
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|client
operator|=
name|HadoopServer
operator|.
name|this
operator|.
name|getJobClient
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|client
operator|=
literal|null
expr_stmt|;
return|return
operator|new
name|Status
argument_list|(
name|Status
operator|.
name|ERROR
argument_list|,
name|Activator
operator|.
name|PLUGIN_ID
argument_list|,
literal|0
argument_list|,
literal|"Cannot connect to the Map/Reduce location: "
operator|+
name|HadoopServer
operator|.
name|this
operator|.
name|getLocationName
argument_list|()
argument_list|,
name|ioe
argument_list|)
return|;
block|}
block|}
try|try
block|{
comment|// Set of all known existing Job IDs we want fresh info of
name|Set
argument_list|<
name|JobID
argument_list|>
name|missingJobIds
init|=
operator|new
name|HashSet
argument_list|<
name|JobID
argument_list|>
argument_list|(
name|runningJobs
operator|.
name|keySet
argument_list|()
argument_list|)
decl_stmt|;
name|JobStatus
index|[]
name|jstatus
init|=
name|client
operator|.
name|jobsToComplete
argument_list|()
decl_stmt|;
for|for
control|(
name|JobStatus
name|status
range|:
name|jstatus
control|)
block|{
name|JobID
name|jobId
init|=
name|status
operator|.
name|getJobID
argument_list|()
decl_stmt|;
name|missingJobIds
operator|.
name|remove
argument_list|(
name|jobId
argument_list|)
expr_stmt|;
name|HadoopJob
name|hJob
decl_stmt|;
synchronized|synchronized
init|(
name|HadoopServer
operator|.
name|this
operator|.
name|runningJobs
init|)
block|{
name|hJob
operator|=
name|runningJobs
operator|.
name|get
argument_list|(
name|jobId
argument_list|)
expr_stmt|;
if|if
condition|(
name|hJob
operator|==
literal|null
condition|)
block|{
comment|// Unknown job, create an entry
name|RunningJob
name|running
init|=
name|client
operator|.
name|getJob
argument_list|(
name|jobId
argument_list|)
decl_stmt|;
name|hJob
operator|=
operator|new
name|HadoopJob
argument_list|(
name|HadoopServer
operator|.
name|this
argument_list|,
name|jobId
argument_list|,
name|running
argument_list|,
name|status
argument_list|)
expr_stmt|;
name|newJob
argument_list|(
name|hJob
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Update HadoopJob with fresh infos
name|updateJob
argument_list|(
name|hJob
argument_list|,
name|status
argument_list|)
expr_stmt|;
block|}
comment|// Ask explicitly for fresh info for these Job IDs
for|for
control|(
name|JobID
name|jobId
range|:
name|missingJobIds
control|)
block|{
name|HadoopJob
name|hJob
init|=
name|runningJobs
operator|.
name|get
argument_list|(
name|jobId
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|hJob
operator|.
name|isCompleted
argument_list|()
condition|)
name|updateJob
argument_list|(
name|hJob
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|client
operator|=
literal|null
expr_stmt|;
return|return
operator|new
name|Status
argument_list|(
name|Status
operator|.
name|ERROR
argument_list|,
name|Activator
operator|.
name|PLUGIN_ID
argument_list|,
literal|0
argument_list|,
literal|"Cannot retrieve running Jobs on location: "
operator|+
name|HadoopServer
operator|.
name|this
operator|.
name|getLocationName
argument_list|()
argument_list|,
name|ioe
argument_list|)
return|;
block|}
comment|// Schedule the next observation
name|schedule
argument_list|(
name|STATUS_OBSERVATION_DELAY
argument_list|)
expr_stmt|;
return|return
name|Status
operator|.
name|OK_STATUS
return|;
block|}
comment|/**      * Stores and make the new job available      *       * @param data      */
DECL|method|newJob (final HadoopJob data)
specifier|private
name|void
name|newJob
parameter_list|(
specifier|final
name|HadoopJob
name|data
parameter_list|)
block|{
name|runningJobs
operator|.
name|put
argument_list|(
name|data
operator|.
name|getJobID
argument_list|()
argument_list|,
name|data
argument_list|)
expr_stmt|;
name|Display
operator|.
name|getDefault
argument_list|()
operator|.
name|asyncExec
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|fireJobAdded
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**      * Updates the status of a job      *       * @param job the job to update      */
DECL|method|updateJob (final HadoopJob job, JobStatus status)
specifier|private
name|void
name|updateJob
parameter_list|(
specifier|final
name|HadoopJob
name|job
parameter_list|,
name|JobStatus
name|status
parameter_list|)
block|{
name|job
operator|.
name|update
argument_list|(
name|status
argument_list|)
expr_stmt|;
name|Display
operator|.
name|getDefault
argument_list|()
operator|.
name|asyncExec
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|fireJobChanged
argument_list|(
name|job
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
DECL|field|log
specifier|static
name|Logger
name|log
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|HadoopServer
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
comment|/**    * Hadoop configuration of the location. Also contains specific parameters    * for the plug-in. These parameters are prefix with eclipse.plug-in.*    */
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
comment|/**    * Jobs listeners    */
DECL|field|jobListeners
specifier|private
name|Set
argument_list|<
name|IJobListener
argument_list|>
name|jobListeners
init|=
operator|new
name|HashSet
argument_list|<
name|IJobListener
argument_list|>
argument_list|()
decl_stmt|;
comment|/**    * Jobs running on this location. The keys of this map are the Job IDs.    */
DECL|field|runningJobs
specifier|private
specifier|transient
name|Map
argument_list|<
name|JobID
argument_list|,
name|HadoopJob
argument_list|>
name|runningJobs
init|=
name|Collections
operator|.
name|synchronizedMap
argument_list|(
operator|new
name|TreeMap
argument_list|<
name|JobID
argument_list|,
name|HadoopJob
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
comment|/**    * Status updater for this location    */
DECL|field|statusUpdater
specifier|private
name|LocationStatusUpdater
name|statusUpdater
decl_stmt|;
comment|// state and status - transient
DECL|field|state
specifier|private
specifier|transient
name|String
name|state
init|=
literal|""
decl_stmt|;
comment|/**    * Creates a new default Hadoop location    */
DECL|method|HadoopServer ()
specifier|public
name|HadoopServer
parameter_list|()
block|{
name|this
operator|.
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|this
operator|.
name|addPluginConfigDefaultProperties
argument_list|()
expr_stmt|;
block|}
comment|/**    * Creates a location from a file    *     * @throws IOException    * @throws SAXException    * @throws ParserConfigurationException    */
DECL|method|HadoopServer (File file)
specifier|public
name|HadoopServer
parameter_list|(
name|File
name|file
parameter_list|)
throws|throws
name|ParserConfigurationException
throws|,
name|SAXException
throws|,
name|IOException
block|{
name|this
operator|.
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|this
operator|.
name|addPluginConfigDefaultProperties
argument_list|()
expr_stmt|;
name|this
operator|.
name|loadFromXML
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a new Hadoop location by copying an already existing one.    *     * @param source the location to copy    */
DECL|method|HadoopServer (HadoopServer existing)
specifier|public
name|HadoopServer
parameter_list|(
name|HadoopServer
name|existing
parameter_list|)
block|{
name|this
argument_list|()
expr_stmt|;
name|this
operator|.
name|load
argument_list|(
name|existing
argument_list|)
expr_stmt|;
block|}
DECL|method|addJobListener (IJobListener l)
specifier|public
name|void
name|addJobListener
parameter_list|(
name|IJobListener
name|l
parameter_list|)
block|{
name|jobListeners
operator|.
name|add
argument_list|(
name|l
argument_list|)
expr_stmt|;
block|}
DECL|method|dispose ()
specifier|public
name|void
name|dispose
parameter_list|()
block|{
comment|// TODO close DFS connections?
block|}
comment|/**    * List all elements that should be present in the Server window (all    * servers and all jobs running on each servers)    *     * @return collection of jobs for this location    */
DECL|method|getJobs ()
specifier|public
name|Collection
argument_list|<
name|HadoopJob
argument_list|>
name|getJobs
parameter_list|()
block|{
name|startStatusUpdater
argument_list|()
expr_stmt|;
return|return
name|this
operator|.
name|runningJobs
operator|.
name|values
argument_list|()
return|;
block|}
comment|/**    * Remove the given job from the currently running jobs map    *     * @param job the job to remove    */
DECL|method|purgeJob (final HadoopJob job)
specifier|public
name|void
name|purgeJob
parameter_list|(
specifier|final
name|HadoopJob
name|job
parameter_list|)
block|{
name|runningJobs
operator|.
name|remove
argument_list|(
name|job
operator|.
name|getJobID
argument_list|()
argument_list|)
expr_stmt|;
name|Display
operator|.
name|getDefault
argument_list|()
operator|.
name|asyncExec
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|fireJobRemoved
argument_list|(
name|job
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns the {@link Configuration} defining this location.    *     * @return the location configuration    */
DECL|method|getConfiguration ()
specifier|public
name|Configuration
name|getConfiguration
parameter_list|()
block|{
return|return
name|this
operator|.
name|conf
return|;
block|}
comment|/**    * Gets a Hadoop configuration property value    *     * @param prop the configuration property    * @return the property value    */
DECL|method|getConfProp (ConfProp prop)
specifier|public
name|String
name|getConfProp
parameter_list|(
name|ConfProp
name|prop
parameter_list|)
block|{
return|return
name|prop
operator|.
name|get
argument_list|(
name|conf
argument_list|)
return|;
block|}
comment|/**    * Gets a Hadoop configuration property value    *     * @param propName the property name    * @return the property value    */
DECL|method|getConfProp (String propName)
specifier|public
name|String
name|getConfProp
parameter_list|(
name|String
name|propName
parameter_list|)
block|{
return|return
name|this
operator|.
name|conf
operator|.
name|get
argument_list|(
name|propName
argument_list|)
return|;
block|}
DECL|method|getLocationName ()
specifier|public
name|String
name|getLocationName
parameter_list|()
block|{
return|return
name|ConfProp
operator|.
name|PI_LOCATION_NAME
operator|.
name|get
argument_list|(
name|conf
argument_list|)
return|;
block|}
comment|/**    * Returns the master host name of the Hadoop location (the Job tracker)    *     * @return the host name of the Job tracker    */
DECL|method|getMasterHostName ()
specifier|public
name|String
name|getMasterHostName
parameter_list|()
block|{
return|return
name|getConfProp
argument_list|(
name|ConfProp
operator|.
name|PI_JOB_TRACKER_HOST
argument_list|)
return|;
block|}
DECL|method|getState ()
specifier|public
name|String
name|getState
parameter_list|()
block|{
return|return
name|state
return|;
block|}
comment|/**    * Overwrite this location with the given existing location    *     * @param existing the existing location    */
DECL|method|load (HadoopServer existing)
specifier|public
name|void
name|load
parameter_list|(
name|HadoopServer
name|existing
parameter_list|)
block|{
name|this
operator|.
name|conf
operator|=
operator|new
name|Configuration
argument_list|(
name|existing
operator|.
name|conf
argument_list|)
expr_stmt|;
block|}
comment|/**    * Overwrite this location with settings available in the given XML file.    * The existing configuration is preserved if the XML file is invalid.    *     * @param file the file path of the XML file    * @return validity of the XML file    * @throws ParserConfigurationException    * @throws IOException    * @throws SAXException    */
DECL|method|loadFromXML (File file)
specifier|public
name|boolean
name|loadFromXML
parameter_list|(
name|File
name|file
parameter_list|)
throws|throws
name|ParserConfigurationException
throws|,
name|SAXException
throws|,
name|IOException
block|{
name|Configuration
name|newConf
init|=
operator|new
name|Configuration
argument_list|(
name|this
operator|.
name|conf
argument_list|)
decl_stmt|;
name|DocumentBuilder
name|builder
init|=
name|DocumentBuilderFactory
operator|.
name|newInstance
argument_list|()
operator|.
name|newDocumentBuilder
argument_list|()
decl_stmt|;
name|Document
name|document
init|=
name|builder
operator|.
name|parse
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|Element
name|root
init|=
name|document
operator|.
name|getDocumentElement
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
literal|"configuration"
operator|.
name|equals
argument_list|(
name|root
operator|.
name|getTagName
argument_list|()
argument_list|)
condition|)
return|return
literal|false
return|;
name|NodeList
name|props
init|=
name|root
operator|.
name|getChildNodes
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
name|props
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Node
name|propNode
init|=
name|props
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|propNode
operator|instanceof
name|Element
operator|)
condition|)
continue|continue;
name|Element
name|prop
init|=
operator|(
name|Element
operator|)
name|propNode
decl_stmt|;
if|if
condition|(
operator|!
literal|"property"
operator|.
name|equals
argument_list|(
name|prop
operator|.
name|getTagName
argument_list|()
argument_list|)
condition|)
return|return
literal|false
return|;
name|NodeList
name|fields
init|=
name|prop
operator|.
name|getChildNodes
argument_list|()
decl_stmt|;
name|String
name|attr
init|=
literal|null
decl_stmt|;
name|String
name|value
init|=
literal|null
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|fields
operator|.
name|getLength
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
name|Node
name|fieldNode
init|=
name|fields
operator|.
name|item
argument_list|(
name|j
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|fieldNode
operator|instanceof
name|Element
operator|)
condition|)
continue|continue;
name|Element
name|field
init|=
operator|(
name|Element
operator|)
name|fieldNode
decl_stmt|;
if|if
condition|(
literal|"name"
operator|.
name|equals
argument_list|(
name|field
operator|.
name|getTagName
argument_list|()
argument_list|)
condition|)
name|attr
operator|=
operator|(
operator|(
name|Text
operator|)
name|field
operator|.
name|getFirstChild
argument_list|()
operator|)
operator|.
name|getData
argument_list|()
expr_stmt|;
if|if
condition|(
literal|"value"
operator|.
name|equals
argument_list|(
name|field
operator|.
name|getTagName
argument_list|()
argument_list|)
operator|&&
name|field
operator|.
name|hasChildNodes
argument_list|()
condition|)
name|value
operator|=
operator|(
operator|(
name|Text
operator|)
name|field
operator|.
name|getFirstChild
argument_list|()
operator|)
operator|.
name|getData
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|attr
operator|!=
literal|null
operator|&&
name|value
operator|!=
literal|null
condition|)
name|newConf
operator|.
name|set
argument_list|(
name|attr
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|conf
operator|=
name|newConf
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|/**    * Sets a Hadoop configuration property value    *     * @param prop the property    * @param propvalue the property value    */
DECL|method|setConfProp (ConfProp prop, String propValue)
specifier|public
name|void
name|setConfProp
parameter_list|(
name|ConfProp
name|prop
parameter_list|,
name|String
name|propValue
parameter_list|)
block|{
name|prop
operator|.
name|set
argument_list|(
name|conf
argument_list|,
name|propValue
argument_list|)
expr_stmt|;
block|}
comment|/**    * Sets a Hadoop configuration property value    *     * @param propName the property name    * @param propValue the property value    */
DECL|method|setConfProp (String propName, String propValue)
specifier|public
name|void
name|setConfProp
parameter_list|(
name|String
name|propName
parameter_list|,
name|String
name|propValue
parameter_list|)
block|{
name|this
operator|.
name|conf
operator|.
name|set
argument_list|(
name|propName
argument_list|,
name|propValue
argument_list|)
expr_stmt|;
block|}
DECL|method|setLocationName (String newName)
specifier|public
name|void
name|setLocationName
parameter_list|(
name|String
name|newName
parameter_list|)
block|{
name|ConfProp
operator|.
name|PI_LOCATION_NAME
operator|.
name|set
argument_list|(
name|conf
argument_list|,
name|newName
argument_list|)
expr_stmt|;
block|}
comment|/**    * Write this location settings to the given output stream    *     * @param out the output stream    * @throws IOException    */
DECL|method|storeSettingsToFile (File file)
specifier|public
name|void
name|storeSettingsToFile
parameter_list|(
name|File
name|file
parameter_list|)
throws|throws
name|IOException
block|{
name|FileOutputStream
name|fos
init|=
operator|new
name|FileOutputStream
argument_list|(
name|file
argument_list|)
decl_stmt|;
try|try
block|{
name|this
operator|.
name|conf
operator|.
name|writeXml
argument_list|(
name|fos
argument_list|)
expr_stmt|;
name|fos
operator|.
name|close
argument_list|()
expr_stmt|;
name|fos
operator|=
literal|null
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|fos
argument_list|)
expr_stmt|;
block|}
block|}
comment|/* @inheritDoc */
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|this
operator|.
name|getLocationName
argument_list|()
return|;
block|}
comment|/**    * Fill the configuration with valid default values    */
DECL|method|addPluginConfigDefaultProperties ()
specifier|private
name|void
name|addPluginConfigDefaultProperties
parameter_list|()
block|{
for|for
control|(
name|ConfProp
name|prop
range|:
name|ConfProp
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|conf
operator|.
name|get
argument_list|(
name|prop
operator|.
name|name
argument_list|)
operator|==
literal|null
condition|)
name|conf
operator|.
name|set
argument_list|(
name|prop
operator|.
name|name
argument_list|,
name|prop
operator|.
name|defVal
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Starts the location status updater    */
DECL|method|startStatusUpdater ()
specifier|private
specifier|synchronized
name|void
name|startStatusUpdater
parameter_list|()
block|{
if|if
condition|(
name|statusUpdater
operator|==
literal|null
condition|)
block|{
name|statusUpdater
operator|=
operator|new
name|LocationStatusUpdater
argument_list|()
expr_stmt|;
name|statusUpdater
operator|.
name|schedule
argument_list|()
expr_stmt|;
block|}
block|}
comment|/*    * Rewrite of the connecting and tunneling to the Hadoop location    */
comment|/**    * Provides access to the default file system of this location.    *     * @return a {@link FileSystem}    */
DECL|method|getDFS ()
specifier|public
name|FileSystem
name|getDFS
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|FileSystem
operator|.
name|get
argument_list|(
name|this
operator|.
name|conf
argument_list|)
return|;
block|}
comment|/**    * Provides access to the Job tracking system of this location    *     * @return a {@link JobClient}    */
DECL|method|getJobClient ()
specifier|public
name|JobClient
name|getJobClient
parameter_list|()
throws|throws
name|IOException
block|{
name|JobConf
name|jconf
init|=
operator|new
name|JobConf
argument_list|(
name|this
operator|.
name|conf
argument_list|)
decl_stmt|;
return|return
operator|new
name|JobClient
argument_list|(
name|jconf
argument_list|)
return|;
block|}
comment|/*    * Listeners handling    */
DECL|method|fireJarPublishDone (JarModule jar)
specifier|protected
name|void
name|fireJarPublishDone
parameter_list|(
name|JarModule
name|jar
parameter_list|)
block|{
for|for
control|(
name|IJobListener
name|listener
range|:
name|jobListeners
control|)
block|{
name|listener
operator|.
name|publishDone
argument_list|(
name|jar
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|fireJarPublishStart (JarModule jar)
specifier|protected
name|void
name|fireJarPublishStart
parameter_list|(
name|JarModule
name|jar
parameter_list|)
block|{
for|for
control|(
name|IJobListener
name|listener
range|:
name|jobListeners
control|)
block|{
name|listener
operator|.
name|publishStart
argument_list|(
name|jar
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|fireJobAdded (HadoopJob job)
specifier|protected
name|void
name|fireJobAdded
parameter_list|(
name|HadoopJob
name|job
parameter_list|)
block|{
for|for
control|(
name|IJobListener
name|listener
range|:
name|jobListeners
control|)
block|{
name|listener
operator|.
name|jobAdded
argument_list|(
name|job
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|fireJobRemoved (HadoopJob job)
specifier|protected
name|void
name|fireJobRemoved
parameter_list|(
name|HadoopJob
name|job
parameter_list|)
block|{
for|for
control|(
name|IJobListener
name|listener
range|:
name|jobListeners
control|)
block|{
name|listener
operator|.
name|jobRemoved
argument_list|(
name|job
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|fireJobChanged (HadoopJob job)
specifier|protected
name|void
name|fireJobChanged
parameter_list|(
name|HadoopJob
name|job
parameter_list|)
block|{
for|for
control|(
name|IJobListener
name|listener
range|:
name|jobListeners
control|)
block|{
name|listener
operator|.
name|jobChanged
argument_list|(
name|job
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

