begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|nodemanager
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
import|;
end_import

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
name|FileNotFoundException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileReader
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
name|junit
operator|.
name|framework
operator|.
name|Assert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|UnsupportedFileSystemException
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
name|AccessControlException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|TestLinuxContainerExecutor
specifier|public
class|class
name|TestLinuxContainerExecutor
block|{
comment|//
comment|//  private static final Log LOG = LogFactory
comment|//      .getLog(TestLinuxContainerExecutor.class);
comment|//
comment|//  // TODO: FIXME
comment|//  private static File workSpace = new File("target",
comment|//      TestLinuxContainerExecutor.class.getName() + "-workSpace");
comment|//
comment|//  @Before
comment|//  public void setup() throws IOException {
comment|//    FileContext.getLocalFSFileContext().mkdir(
comment|//        new Path(workSpace.getAbsolutePath()), null, true);
comment|//    workSpace.setReadable(true, false);
comment|//    workSpace.setExecutable(true, false);
comment|//    workSpace.setWritable(true, false);
comment|//  }
comment|//
comment|//  @After
comment|//  public void tearDown() throws AccessControlException, FileNotFoundException,
comment|//      UnsupportedFileSystemException, IOException {
comment|//    FileContext.getLocalFSFileContext().delete(
comment|//        new Path(workSpace.getAbsolutePath()), true);
comment|//  }
comment|//
annotation|@
name|Test
DECL|method|testCommandFilePreparation ()
specifier|public
name|void
name|testCommandFilePreparation
parameter_list|()
throws|throws
name|IOException
block|{
comment|//    LinuxContainerExecutor executor = new LinuxContainerExecutor(new String[] {
comment|//        "/bin/echo", "hello" }, null, null, "nobody"); // TODO: fix user name
comment|//    executor.prepareCommandFile(workSpace.getAbsolutePath());
comment|//
comment|//    // Now verify the contents of the commandFile
comment|//    File commandFile = new File(workSpace, LinuxContainerExecutor.COMMAND_FILE);
comment|//    BufferedReader reader = new BufferedReader(new FileReader(commandFile));
comment|//    Assert.assertEquals("/bin/echo hello", reader.readLine());
comment|//    Assert.assertEquals(null, reader.readLine());
comment|//    Assert.assertTrue(commandFile.canExecute());
block|}
comment|//
comment|//  @Test
comment|//  public void testContainerLaunch() throws IOException {
comment|//    String containerExecutorPath = System
comment|//        .getProperty("container-executor-path");
comment|//    if (containerExecutorPath == null || containerExecutorPath.equals("")) {
comment|//      LOG.info("Not Running test for lack of container-executor-path");
comment|//      return;
comment|//    }
comment|//
comment|//    String applicationSubmitter = "nobody";
comment|//
comment|//    File touchFile = new File(workSpace, "touch-file");
comment|//    LinuxContainerExecutor executor = new LinuxContainerExecutor(new String[] {
comment|//        "touch", touchFile.getAbsolutePath() }, workSpace, null,
comment|//        applicationSubmitter);
comment|//    executor.setCommandExecutorPath(containerExecutorPath);
comment|//    executor.execute();
comment|//
comment|//    FileStatus fileStatus = FileContext.getLocalFSFileContext().getFileStatus(
comment|//        new Path(touchFile.getAbsolutePath()));
comment|//    Assert.assertEquals(applicationSubmitter, fileStatus.getOwner());
comment|//  }
comment|//
comment|//  @Test
comment|//  public void testContainerKill() throws IOException, InterruptedException,
comment|//      IllegalArgumentException, SecurityException, IllegalAccessException,
comment|//      NoSuchFieldException {
comment|//    String containerExecutorPath = System
comment|//        .getProperty("container-executor-path");
comment|//    if (containerExecutorPath == null || containerExecutorPath.equals("")) {
comment|//      LOG.info("Not Running test for lack of container-executor-path");
comment|//      return;
comment|//    }
comment|//
comment|//    String applicationSubmitter = "nobody";
comment|//    final LinuxContainerExecutor executor = new LinuxContainerExecutor(
comment|//        new String[] { "sleep", "100" }, workSpace, null, applicationSubmitter);
comment|//    executor.setCommandExecutorPath(containerExecutorPath);
comment|//    new Thread() {
comment|//      public void run() {
comment|//        try {
comment|//          executor.execute();
comment|//        } catch (IOException e) {
comment|//          // TODO Auto-generated catch block
comment|//          e.printStackTrace();
comment|//        }
comment|//      };
comment|//    }.start();
comment|//
comment|//    String pid;
comment|//    while ((pid = executor.getPid()) == null) {
comment|//      LOG.info("Sleeping for 5 seconds before checking if "
comment|//          + "the process is alive.");
comment|//      Thread.sleep(5000);
comment|//    }
comment|//    LOG.info("Going to check the liveliness of the process with pid " + pid);
comment|//
comment|//    LinuxContainerExecutor checkLiveliness = new LinuxContainerExecutor(
comment|//        new String[] { "kill", "-0", "-" + pid }, workSpace, null,
comment|//        applicationSubmitter);
comment|//    checkLiveliness.setCommandExecutorPath(containerExecutorPath);
comment|//    checkLiveliness.execute();
comment|//
comment|//    LOG.info("Process is alive. "
comment|//        + "Sleeping for 5 seconds before killing the process.");
comment|//    Thread.sleep(5000);
comment|//    LOG.info("Going to killing the process.");
comment|//
comment|//    executor.kill();
comment|//
comment|//    LOG.info("Sleeping for 5 seconds before checking if "
comment|//        + "the process is alive.");
comment|//    Thread.sleep(5000);
comment|//    LOG.info("Going to check the liveliness of the process.");
comment|//
comment|//    // TODO: fix
comment|//    checkLiveliness = new LinuxContainerExecutor(new String[] { "kill", "-0",
comment|//        "-" + pid }, workSpace, null, applicationSubmitter);
comment|//    checkLiveliness.setCommandExecutorPath(containerExecutorPath);
comment|//    boolean success = false;
comment|//    try {
comment|//      checkLiveliness.execute();
comment|//      success = true;
comment|//    } catch (IOException e) {
comment|//      success = false;
comment|//    }
comment|//
comment|//    Assert.assertFalse(success);
comment|//  }
block|}
end_class

end_unit

