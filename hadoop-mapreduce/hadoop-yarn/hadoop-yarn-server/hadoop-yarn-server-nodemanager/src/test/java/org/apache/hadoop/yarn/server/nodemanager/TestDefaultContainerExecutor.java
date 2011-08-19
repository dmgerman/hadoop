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
name|InputStream
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
name|InetSocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EnumSet
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
name|Random
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
name|AbstractFileSystem
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
name|FSDataInputStream
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
name|FSDataOutputStream
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
name|Options
operator|.
name|CreateOpts
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
name|io
operator|.
name|DataInputBuffer
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
name|DataOutputBuffer
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
name|util
operator|.
name|Progressable
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
name|server
operator|.
name|nodemanager
operator|.
name|containermanager
operator|.
name|localizer
operator|.
name|FakeFSDataInputStream
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|CreateFlag
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
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

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|ArgumentMatcher
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|Matchers
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|*
import|;
end_import

begin_class
DECL|class|TestDefaultContainerExecutor
specifier|public
class|class
name|TestDefaultContainerExecutor
block|{
comment|/*   // XXX FileContext cannot be mocked to do this   static FSDataInputStream getRandomStream(Random r, int len)       throws IOException {     byte[] bytes = new byte[len];     r.nextBytes(bytes);     DataInputBuffer buf = new DataInputBuffer();     buf.reset(bytes, 0, bytes.length);     return new FSDataInputStream(new FakeFSDataInputStream(buf));   }    class PathEndsWith extends ArgumentMatcher<Path> {     final String suffix;     PathEndsWith(String suffix) {       this.suffix = suffix;     }     @Override     public boolean matches(Object o) {       return       suffix.equals(((Path)o).getName());     }   }    DataOutputBuffer mockStream(       AbstractFileSystem spylfs, Path p, Random r, int len)        throws IOException {     DataOutputBuffer dob = new DataOutputBuffer();     doReturn(getRandomStream(r, len)).when(spylfs).open(p);     doReturn(new FileStatus(len, false, -1, -1L, -1L, p)).when(         spylfs).getFileStatus(argThat(new PathEndsWith(p.getName())));     doReturn(new FSDataOutputStream(dob)).when(spylfs).createInternal(         argThat(new PathEndsWith(p.getName())),         eq(EnumSet.of(OVERWRITE)),         Matchers.<FsPermission>anyObject(), anyInt(), anyShort(), anyLong(),         Matchers.<Progressable>anyObject(), anyInt(), anyBoolean());     return dob;   }   */
annotation|@
name|AfterClass
DECL|method|deleteTmpFiles ()
specifier|public
specifier|static
name|void
name|deleteTmpFiles
parameter_list|()
throws|throws
name|IOException
block|{
name|FileContext
name|lfs
init|=
name|FileContext
operator|.
name|getLocalFSFileContext
argument_list|()
decl_stmt|;
name|lfs
operator|.
name|delete
argument_list|(
operator|new
name|Path
argument_list|(
literal|"target"
argument_list|,
name|TestDefaultContainerExecutor
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|createTmpFile (Path dst, Random r, int len)
name|byte
index|[]
name|createTmpFile
parameter_list|(
name|Path
name|dst
parameter_list|,
name|Random
name|r
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
comment|// use unmodified local context
name|FileContext
name|lfs
init|=
name|FileContext
operator|.
name|getLocalFSFileContext
argument_list|()
decl_stmt|;
name|dst
operator|=
name|lfs
operator|.
name|makeQualified
argument_list|(
name|dst
argument_list|)
expr_stmt|;
name|lfs
operator|.
name|mkdir
argument_list|(
name|dst
operator|.
name|getParent
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
name|len
index|]
decl_stmt|;
name|FSDataOutputStream
name|out
init|=
literal|null
decl_stmt|;
try|try
block|{
name|out
operator|=
name|lfs
operator|.
name|create
argument_list|(
name|dst
argument_list|,
name|EnumSet
operator|.
name|of
argument_list|(
name|CREATE
argument_list|,
name|OVERWRITE
argument_list|)
argument_list|)
expr_stmt|;
name|r
operator|.
name|nextBytes
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|out
operator|!=
literal|null
condition|)
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|bytes
return|;
block|}
comment|//  @Test
comment|//  public void testInit() throws IOException, InterruptedException {
comment|//    Configuration conf = new Configuration();
comment|//    AbstractFileSystem spylfs =
comment|//      spy(FileContext.getLocalFSFileContext().getDefaultFileSystem());
comment|//    // don't actually create dirs
comment|//    //doNothing().when(spylfs).mkdir(Matchers.<Path>anyObject(),
comment|//    //    Matchers.<FsPermission>anyObject(), anyBoolean());
comment|//    FileContext lfs = FileContext.getFileContext(spylfs, conf);
comment|//
comment|//    Path basedir = new Path("target",
comment|//        TestDefaultContainerExecutor.class.getSimpleName());
comment|//    List<String> localDirs = new ArrayList<String>();
comment|//    List<Path> localPaths = new ArrayList<Path>();
comment|//    for (int i = 0; i< 4; ++i) {
comment|//      Path p = new Path(basedir, i + "");
comment|//      lfs.mkdir(p, null, true);
comment|//      localPaths.add(p);
comment|//      localDirs.add(p.toString());
comment|//    }
comment|//    final String user = "yak";
comment|//    final String appId = "app_RM_0";
comment|//    final Path logDir = new Path(basedir, "logs");
comment|//    final Path nmLocal = new Path(basedir, "nmPrivate/" + user + "/" + appId);
comment|//    final InetSocketAddress nmAddr = new InetSocketAddress("foobar", 4344);
comment|//    System.out.println("NMLOCAL: " + nmLocal);
comment|//    Random r = new Random();
comment|//
comment|//    /*
comment|//    // XXX FileContext cannot be reasonably mocked to do this
comment|//    // mock jobFiles copy
comment|//    long fileSeed = r.nextLong();
comment|//    r.setSeed(fileSeed);
comment|//    System.out.println("SEED: " + seed);
comment|//    Path fileCachePath = new Path(nmLocal, ApplicationLocalizer.FILECACHE_FILE);
comment|//    DataOutputBuffer fileCacheBytes = mockStream(spylfs, fileCachePath, r, 512);
comment|//
comment|//    // mock jobTokens copy
comment|//    long jobSeed = r.nextLong();
comment|//    r.setSeed(jobSeed);
comment|//    System.out.println("SEED: " + seed);
comment|//    Path jobTokenPath = new Path(nmLocal, ApplicationLocalizer.JOBTOKEN_FILE);
comment|//    DataOutputBuffer jobTokenBytes = mockStream(spylfs, jobTokenPath, r, 512);
comment|//    */
comment|//
comment|//    // create jobFiles
comment|//    long fileSeed = r.nextLong();
comment|//    r.setSeed(fileSeed);
comment|//    System.out.println("SEED: " + fileSeed);
comment|//    Path fileCachePath = new Path(nmLocal, ApplicationLocalizer.FILECACHE_FILE);
comment|//    byte[] fileCacheBytes = createTmpFile(fileCachePath, r, 512);
comment|//
comment|//    // create jobTokens
comment|//    long jobSeed = r.nextLong();
comment|//    r.setSeed(jobSeed);
comment|//    System.out.println("SEED: " + jobSeed);
comment|//    Path jobTokenPath = new Path(nmLocal, ApplicationLocalizer.JOBTOKEN_FILE);
comment|//    byte[] jobTokenBytes = createTmpFile(jobTokenPath, r, 512);
comment|//
comment|//    DefaultContainerExecutor dce = new DefaultContainerExecutor(lfs);
comment|//    Localization mockLocalization = mock(Localization.class);
comment|//    ApplicationLocalizer spyLocalizer =
comment|//      spy(new ApplicationLocalizer(lfs, user, appId, logDir,
comment|//            localPaths));
comment|//    // ignore cache localization
comment|//    doNothing().when(spyLocalizer).localizeFiles(
comment|//        Matchers.<Localization>anyObject(), Matchers.<Path>anyObject());
comment|//    Path workingDir = lfs.getWorkingDirectory();
comment|//    dce.initApplication(spyLocalizer, nmLocal, mockLocalization, localPaths);
comment|//    lfs.setWorkingDirectory(workingDir);
comment|//
comment|//    for (Path localdir : localPaths) {
comment|//      Path userdir = lfs.makeQualified(new Path(localdir,
comment|//            new Path(ApplicationLocalizer.USERCACHE, user)));
comment|//      // $localdir/$user
comment|//      verify(spylfs).mkdir(userdir,
comment|//          new FsPermission(ApplicationLocalizer.USER_PERM), true);
comment|//      // $localdir/$user/appcache
comment|//      Path jobdir = new Path(userdir, ApplicationLocalizer.appcache);
comment|//      verify(spylfs).mkdir(jobdir,
comment|//          new FsPermission(ApplicationLocalizer.appcache_PERM), true);
comment|//      // $localdir/$user/filecache
comment|//      Path filedir = new Path(userdir, ApplicationLocalizer.FILECACHE);
comment|//      verify(spylfs).mkdir(filedir,
comment|//          new FsPermission(ApplicationLocalizer.FILECACHE_PERM), true);
comment|//      // $localdir/$user/appcache/$appId
comment|//      Path appdir = new Path(jobdir, appId);
comment|//      verify(spylfs).mkdir(appdir,
comment|//          new FsPermission(ApplicationLocalizer.APPDIR_PERM), true);
comment|//      // $localdir/$user/appcache/$appId/work
comment|//      Path workdir = new Path(appdir, ApplicationLocalizer.WORKDIR);
comment|//      verify(spylfs, atMost(1)).mkdir(workdir, FsPermission.getDefault(), true);
comment|//    }
comment|//    // $logdir/$appId
comment|//    Path logdir = new Path(lfs.makeQualified(logDir), appId);
comment|//    verify(spylfs).mkdir(logdir,
comment|//        new FsPermission(ApplicationLocalizer.LOGDIR_PERM), true);
comment|//  }
block|}
end_class

end_unit

