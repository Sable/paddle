/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003, 2004, 2005 Ondrej Lhotak
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package soot.jimple.paddle;
import soot.*;
import soot.jimple.paddle.queue.*;
import soot.jimple.paddle.bdddomains.*;
import soot.options.*;
import java.util.*;
import java.util.zip.*;
import java.io.*;
import jedd.*;
import jedd.order.*;

/** This class puts all of the pieces of Paddle together and connects them
 * with queues.
 * @author Ondrej Lhotak
 */
public class PaddleScene 
{ 
    public PaddleScene( PaddleSingletons.Global g ) {}
    public static PaddleScene v() { return PaddleG.v().soot_jimple_paddle_PaddleScene(); }

    public AbsNodeInfo ni;
    public AbsTypeManager tm;
    public AbsFactory factory;
    public AbsQFactory qFactory;
    private NodeManager nodeManager;

    public Qvar_method_type locals;
    public Qvar_type globals;
    public Qobj_method_type localallocs;
    public Qobj_type globalallocs;

    public P2SetFactory setFactory;
    public P2SetFactory newSetFactory;
    public P2SetFactory oldSetFactory;

    private PaddleOptions options;
    private CGOptions cgoptions = 
        new CGOptions(PhaseOptions.v().getPhaseOptions("cg"));

    public NodeManager nodeManager() { return nodeManager; }
    public PaddleOptions options() { return options; }
    public CGOptions cgoptions() { return cgoptions; }
    
    public FastHierarchy fh;

    private BDDHierarchy bddh;
    public BDDHierarchy BDDHierarchy() {
        if(bddh == null) bddh = new BDDHierarchy();
        return bddh;
    }

    public void setup( PaddleOptions opts ) {
        options = opts;
        if(!options.ignore_types()) fh = Scene.v().getOrMakeFastHierarchy();
        switch( options.backend() ) {
            case PaddleOptions.backend_buddy:
                Jedd.v().setBackend("buddy"); 
                break;
            case PaddleOptions.backend_cudd:
                Jedd.v().setBackend("cudd"); 
                break;
            case PaddleOptions.backend_sable:
                Jedd.v().setBackend("sablejbdd"); 
                break;
            case PaddleOptions.backend_javabdd:
                Jedd.v().setBackend("javabdd"); 
                break;
            case PaddleOptions.backend_none:
                break;
            default:
                throw new RuntimeException( "Unhandled option: "+options.backend() );
        }
        if( options.backend() != PaddleOptions.backend_none ) {
            int sw;
            if(Scene.v().getContextNumberer() instanceof ContextStringNumberer) {
                sw = ((ContextStringNumberer) Scene.v().getContextNumberer()).shiftWidth;
            } else {
                sw = C1.v().bits();
            }
            int order = options.order();
            if( order == 1 ) {
                Jedd.v().setOrder( 
                    new Seq(
                        new Interleave(
                            new Permute(sw, C1.v()),
                            new Permute(sw, C2.v()),
                            new Permute(sw, C3.v())
                        ),
                        new Interleave(V1.v(), V2.v(), MS.v(), ST.v()),
                        M3.v(),
                        FD.v(),
                        new Interleave(H1.v(), H2.v()),
                        new Interleave(T1.v(), T2.v(), T3.v(), MT.v(), SG.v()),
                        KD.v(),
                        MEASURE1.v(),
                        MEASURE2.v(),
                        MEASURE3.v(),
                        MEASURE4.v()
                    ) );
            } else if( order == 2 ) {
                Jedd.v().setOrder( 
                    new Seq(
                        new AsymInterleave(
                            new Interleave(
                                new Permute(sw, C1.v()),
                                new Permute(sw, C2.v()),
                                new Permute(sw, C3.v())
                            ), 3*4,
                            new Interleave(V1.v(), V2.v()), 2
                        ),
                        new Interleave(MS.v(), ST.v()),
                        M3.v(),
                        FD.v(),
                        new Interleave(H1.v(), H2.v()),
                        new Interleave(T1.v(), T2.v(), T3.v(), MT.v(), SG.v()),
                        KD.v(),
                        MEASURE1.v(),
                        MEASURE2.v(),
                        MEASURE3.v(),
                        MEASURE4.v()
                    ) );
            } else if( order == 3 ) {
                Jedd.v().setOrder( 
                    new Seq(
                        new AsymInterleave(
                            new Interleave(
                                new Permute(sw, C1.v()),
                                new Permute(sw, C2.v()),
                                new Permute(sw, C3.v())
                            ), 3*4,
                            new Interleave(V1.v(), V2.v(), MS.v(), ST.v()), 4
                        ),
                        M3.v(),
                        FD.v(),
                        new Interleave(H1.v(), H2.v()),
                        new Interleave(T1.v(), T2.v(), T3.v(), MT.v(), SG.v()),
                        KD.v(),
                        MEASURE1.v(),
                        MEASURE2.v(),
                        MEASURE3.v(),
                        MEASURE4.v()
                    ) );
            } else if( order == 5 ) {
                Jedd.v().setOrder( 
                    new Seq(
                        new AsymInterleave(
                            new Interleave(
                                new Permute(sw, C1.v()),
                                new Permute(sw, C2.v()),
                                new Permute(sw, C3.v())
                            ), 3*4,
                            new Interleave(V1.v(), V2.v(), MS.v(), ST.v()), 4
                        ),
                        M3.v(),
                        FD.v(),
                        new Interleave(T1.v(), T2.v(), T3.v(), MT.v(), SG.v()),
                        new Interleave(H1.v(), H2.v()),
                        KD.v(),
                        MEASURE1.v(),
                        MEASURE2.v(),
                        MEASURE3.v(),
                        MEASURE4.v()
                    ) );
            } else if( order == 6 ) {
                Jedd.v().setOrder( 
                    new Seq(
                        new Interleave(V1.v(), V2.v(), MS.v(), ST.v()),
                        new Interleave(
                            new Permute(sw, C1.v()),
                            new Permute(sw, C2.v()),
                            new Permute(sw, C3.v())
                        ),
                        M3.v(),
                        FD.v(),
                        new Interleave(T1.v(), T2.v(), T3.v(), MT.v(), SG.v()),
                        new Interleave(H1.v(), H2.v()),
                        KD.v(),
                        MEASURE1.v(),
                        MEASURE2.v(),
                        MEASURE3.v(),
                        MEASURE4.v()
                    ) );
            } else if( order == 7 ) {
                Jedd.v().setOrder( 
                    new Seq(
                        new Interleave(V1.v(), V2.v(), MS.v(), ST.v()),
                        M3.v(),
                        FD.v(),
                        new Interleave(T1.v(), T2.v(), T3.v(), MT.v(), SG.v()),
                        new Interleave(
                            new Permute(sw, C1.v()),
                            new Permute(sw, C2.v()),
                            new Permute(sw, C3.v())
                        ),
                        new Interleave(H1.v(), H2.v()),
                        KD.v(),
                        MEASURE1.v(),
                        MEASURE2.v(),
                        MEASURE3.v(),
                        MEASURE4.v()
                    ) );
            } else if( order == 8 ) {
                Jedd.v().setOrder( 
                    new Seq(
                        new Interleave(V1.v(), V2.v(), MS.v(), ST.v()),
                        M3.v(),
                        FD.v(),
                        new Interleave(T1.v(), T2.v(), T3.v(), MT.v(), SG.v()),
                        new Interleave(H1.v(), H2.v()),
                        KD.v(),
                        new Interleave(
                            new Permute(sw, C1.v()),
                            new Permute(sw, C2.v()),
                            new Permute(sw, C3.v())
                        ),
                        MEASURE1.v(),
                        MEASURE2.v(),
                        MEASURE3.v(),
                        MEASURE4.v()
                    ) );
            } else if( order == 9 ) {
                Jedd.v().setOrder( 
                    new Seq(
                        new Interleave(V1.v(), V2.v(), MS.v(), ST.v()),
                        M3.v(),
                        FD.v(),
                        new Interleave(T1.v(), T2.v(), T3.v(), MT.v(), SG.v()),
                        new Interleave(
                            new Rev(C1.v()),
                            new Rev(C2.v()),
                            new Rev(C3.v())
                        ),
                        new Interleave(H1.v(), H2.v()),
                        KD.v(),
                        MEASURE1.v(),
                        MEASURE2.v(),
                        MEASURE3.v(),
                        MEASURE4.v()
                    ) );
            } else if( order == 10 ) {
                Jedd.v().setOrder( 
                    new Seq(
                        new Interleave(V1.v(), V2.v(), MS.v(), ST.v()),
                        M3.v(),
                        FD.v(),
                        new Interleave(T1.v(), T2.v(), T3.v(), MT.v(), SG.v()),
                        new Interleave(H1.v(), H2.v()),
                        new Interleave(
                            new Rev(C1.v()),
                            new Rev(C2.v()),
                            new Rev(C3.v())
                        ),
                        KD.v(),
                        MEASURE1.v(),
                        MEASURE2.v(),
                        MEASURE3.v(),
                        MEASURE4.v()
                    ) );
            } else if( order == 11 ) {
                Jedd.v().setOrder( 
                    new Seq(
                        new Interleave(V1.v(), V2.v(), MS.v(), ST.v()),
                        M3.v(),
                        FD.v(),
                        new Interleave(T1.v(), T2.v(), T3.v(), MT.v(), SG.v()),
                        new Interleave(H1.v(), H2.v()),
                        new Interleave(
                            C1.v(),
                            C2.v(),
                            C3.v()
                        ),
                        KD.v(),
                        MEASURE1.v(),
                        MEASURE2.v(),
                        MEASURE3.v(),
                        MEASURE4.v()
                    ) );
            } else if( order == 12 ) {
                Jedd.v().setOrder( 
                    new Seq(
                        new Interleave(V1.v(), V2.v(), MS.v(), ST.v()),
                        M3.v(),
                        FD.v(),
                        new Interleave(T1.v(), T2.v(), T3.v(), MT.v(), SG.v()),
                        new Interleave(H1.v(), H2.v()),
                        new Seq(
                            new Rev(C1.v()),
                            new Rev(C2.v()),
                            new Rev(C3.v())
                        ),
                        KD.v(),
                        MEASURE1.v(),
                        MEASURE2.v(),
                        MEASURE3.v(),
                        MEASURE4.v()
                    ) );
            } else if( order == 13 ) {
                Jedd.v().setOrder( 
                    new Seq(
                        new Interleave(V1.v(), V2.v(), MS.v(), ST.v()),
                        M3.v(),
                        FD.v(),
                        new Interleave(T1.v(), T2.v(), T3.v(), MT.v(), SG.v()),
                        H1.v(),
                        H2.v(),
                        new Interleave(
                            new Rev(CH1.v()),
                            new Rev(CH2.v())
                        ),
                        new Interleave(
                            new Rev(C1.v()),
                            new Rev(C2.v()),
                            new Rev(C3.v())
                        ),
                        KD.v(),
                        MEASURE1.v(),
                        MEASURE2.v(),
                        MEASURE3.v(),
                        MEASURE4.v()
                    ) );
            } else if( order == 14 ) {
                Jedd.v().setOrder( 
                    new Seq(
                        new Interleave(V1.v(), V2.v(), MS.v(), ST.v()),
                        M3.v(),
                        FD.v(),
                        new Interleave(T1.v(), T2.v(), T3.v(), MT.v(), SG.v()),
                        H1.v(),
                        H2.v(),
                        new AsymInterleave(
                            new Rev(C1.v()), sw,
                            new Rev(C2.v()), sw,
                            new Rev(C3.v()), sw
                        ),
                        KD.v(),
                        MEASURE1.v(),
                        MEASURE2.v(),
                        MEASURE3.v(),
                        MEASURE4.v()
                    ) );
            } else if( order == 15 ) {
                Jedd.v().setOrder( 
                    new Seq(
                        new Interleave(V1.v(), V2.v(), MS.v(), ST.v()),
                        M3.v(),
                        FD.v(),
                        new Interleave(T1.v(), T2.v(), T3.v(), MT.v(), SG.v()),
                        H1.v(),
                        H2.v(),
                        new Rev(new AsymInterleave(
                            C1.v(), sw,
                            C2.v(), sw,
                            C3.v(), sw
                        )),
                        new Interleave(
                            new Rev(CH1.v()),
                            new Rev(CH2.v())
                        ),
                        KD.v(),
                        MEASURE1.v(),
                        MEASURE2.v(),
                        MEASURE3.v(),
                        MEASURE4.v()
                    ) );
            } else if( order == 16 ) {
                Jedd.v().setOrder( 
                    new Seq(
                        new Interleave(V1.v(), V2.v(), MS.v(), ST.v()),
                        M3.v(),
                        FD.v(),
                        new Interleave(T1.v(), T2.v(), T3.v(), MT.v(), SG.v()),
                        H1.v(),
                        H2.v(),
                        new Interleave(
                            new Rev(C1.v()),
                            new Rev(C2.v()),
                            new Rev(C3.v())
                        ),
                        new Interleave(
                            new Rev(CH1.v()),
                            new Rev(CH2.v())
                        ),
                        KD.v(),
                        MEASURE1.v(),
                        MEASURE2.v(),
                        MEASURE3.v(),
                        MEASURE4.v()
                    ) );
            } else if( order == 17 ) {
                Jedd.v().setOrder( 
                    new Seq(
                        new Interleave(V1.v(), V2.v(), MS.v(), ST.v()),
                        new Rev(C1.v()),
                        M3.v(),
                        FD.v(),
                        new Interleave(T1.v(), T2.v(), T3.v(), MT.v(), SG.v()),
                        H1.v(),
                        H2.v(),
                        new Rev(C2.v()),
                        new Rev(C3.v()),
                        new Interleave(
                            new Rev(CH1.v()),
                            new Rev(CH2.v())
                        ),
                        KD.v(),
                        MEASURE1.v(),
                        MEASURE2.v(),
                        MEASURE3.v(),
                        MEASURE4.v()
                    ) );
            } else if( order == 18 ) {
                Jedd.v().setOrder( 
                    new Seq(
                        new Interleave(V1.v(), V2.v(), MS.v(), ST.v()),
                        M3.v(),
                        FD.v(),
                        new Interleave(T1.v(), T2.v(), T3.v(), MT.v(), SG.v()),
                        H1.v(),
                        H2.v(),
                        new Interleave(
                            new Rev(CH1.v()),
                            new Rev(CH2.v())
                        ),
                        new Rev(C1.v()),
                        new Rev(C2.v()),
                        new Rev(C3.v()),
                        KD.v(),
                        MEASURE1.v(),
                        MEASURE2.v(),
                        MEASURE3.v(),
                        MEASURE4.v()
                    ) );
            } else if( order == 19 ) {
                Jedd.v().setOrder( 
                    new Seq(
                        new Interleave(
                            new Rev(C1.v()),
                            new Rev(C2.v()),
                            new Rev(C3.v())
                        ),
                        new Interleave(V1.v(), V2.v(), MS.v(), ST.v()),
                        M3.v(),
                        FD.v(),
                        new Interleave(T1.v(), T2.v(), T3.v(), MT.v(), SG.v()),
                        H1.v(),
                        H2.v(),
                        new Interleave(
                            new Rev(CH1.v()),
                            new Rev(CH2.v())
                        ),
                        KD.v(),
                        MEASURE1.v(),
                        MEASURE2.v(),
                        MEASURE3.v(),
                        MEASURE4.v()
                    ) );
            } else if( order == 20 ) {
                Jedd.v().setOrder( 
                    new Seq(
                        new Rev(C1.v()),
                        new Interleave(V1.v(), V2.v(), MS.v(), ST.v()),
                        M3.v(),
                        FD.v(),
                        new Rev(C2.v()),
                        new Rev(C3.v()),
                        new Interleave(T1.v(), T2.v(), T3.v(), MT.v(), SG.v()),
                        H1.v(),
                        H2.v(),
                        new Interleave(
                            new Rev(CH1.v()),
                            new Rev(CH2.v())
                        ),
                        KD.v(),
                        MEASURE1.v(),
                        MEASURE2.v(),
                        MEASURE3.v(),
                        MEASURE4.v()
                    ) );
            } else if( order == 21 ) {
                Jedd.v().setOrder( 
                    new Seq(
                        new Interleave(V1.v(), V2.v(), MS.v(), ST.v()),
                        M3.v(),
                        FD.v(),
                        new Interleave(T1.v(), T2.v(), T3.v(), MT.v(), SG.v()),
                        H1.v(),
                        H2.v(),
                        new Interleave(
                            new Rev(CH1.v()),
                            new Rev(CH2.v())
                        ),
                        new Rev(
                            new Interleave(
                                new Rev(C1.v()),
                                new Rev(C2.v()),
                                new Rev(C3.v())
                            )
                        ),
                        KD.v(),
                        MEASURE1.v(),
                        MEASURE2.v(),
                        MEASURE3.v(),
                        MEASURE4.v()
                    ) );
            } else if( order == 22 ) {
                Jedd.v().setOrder( 
                    new Seq(
                        new Interleave(V1.v(), V2.v(), MS.v(), ST.v()),
                        M3.v(),
                        FD.v(),
                        new Interleave(T1.v(), T2.v(), T3.v(), MT.v(), SG.v()),
                        H1.v(),
                        H2.v(),
                        new Interleave(
                            new Rev(CH1.v()),
                            new Rev(CH2.v())
                        ),
                        new AsymInterleave(
                            new Rev(C1.v()), sw,
                            new Rev(C2.v()), sw,
                            new Rev(C3.v()), sw
                        ),
                        KD.v(),
                        MEASURE1.v(),
                        MEASURE2.v(),
                        MEASURE3.v(),
                        MEASURE4.v()
                    ) );
            }
        }
        if( options.profile() ) {
            try {
                Jedd.v().enableProfiling( new PrintStream( new GZIPOutputStream(
                    new FileOutputStream( new File( "profile.sql.gz")))));
            } catch( IOException e ) {
                throw new RuntimeException( "Couldn't output Jedd profile "+e );
            }
        }
        if( options.bdd() ) {
            factory = new BDDFactory();
        } else {
            factory = new TradFactory();
        }
        switch(options.q()) {
            case PaddleOptions.q_trad:
                qFactory = new TradQFactory(); break;
            case PaddleOptions.q_bdd:
                qFactory = new BDDQFactory(); break;
            case PaddleOptions.q_debug:
                qFactory = new DebugQFactory(); break;
            case PaddleOptions.q_trace:
                qFactory = new TraceQFactory(); break;
            case PaddleOptions.q_numtrace:
                qFactory = new NumTraceQFactory(); break;
            default:
                throw new RuntimeException("Unhandled queue type "+options.q());
        }

        makeSetFactories();
        build();

        switch(options.conf()) {
            case PaddleOptions.conf_ofcg:
                config = new OFCGConfig(); break;
            case PaddleOptions.conf_cha:
                config = new CHAConfig(); break;
            case PaddleOptions.conf_cha_aot:
                config = new CHAAOTConfig(); break;
            case PaddleOptions.conf_ofcg_aot:
                config = new OFCGAOTConfig(); break;
            case PaddleOptions.conf_cha_context_aot:
                config = new CHAContextAOTConfig(); break;
            case PaddleOptions.conf_ofcg_context_aot:
                config = new OFCGContextAOTConfig(); break;
            case PaddleOptions.conf_cha_context:
                config = new CHAContextConfig(); break;
            case PaddleOptions.conf_ofcg_context:
                config = new OFCGContextConfig(); break;
            default:
                throw new RuntimeException("Unhandled config type "+options.conf());
        }

        config.setup();
    }

    private AbsConfig config;

    private void makeSetFactories() {
        switch( options.set_impl() ) {
            case SparkOptions.set_impl_hash:
                setFactory = HashPointsToSet.getFactory();
                break;
            case SparkOptions.set_impl_hybrid:
                setFactory = HybridPointsToSet.getFactory();
                break;
            case SparkOptions.set_impl_shared:
                setFactory = SharedPointsToSet.getFactory();
                break;
            case SparkOptions.set_impl_array:
                setFactory = SortedArraySet.getFactory();
                break;
            case SparkOptions.set_impl_bit:
                setFactory = BitPointsToSet.getFactory();
                break;
            case SparkOptions.set_impl_double:
                switch( options.double_set_old() ) {
                    case SparkOptions.double_set_old_hash:
                        oldSetFactory = HashPointsToSet.getFactory();
                        break;
                    case SparkOptions.double_set_old_hybrid:
                        oldSetFactory = HybridPointsToSet.getFactory();
                        break;
                    case SparkOptions.double_set_old_shared:
                        oldSetFactory = SharedPointsToSet.getFactory();
                        break;
                    case SparkOptions.double_set_old_array:
                        oldSetFactory = SortedArraySet.getFactory();
                        break;
                    case SparkOptions.double_set_old_bit:
                        oldSetFactory = BitPointsToSet.getFactory();
                        break;
                    default:
                        throw new RuntimeException();
                }
                switch( options.double_set_new() ) {
                    case SparkOptions.double_set_new_hash:
                        newSetFactory = HashPointsToSet.getFactory();
                        break;
                    case SparkOptions.double_set_new_hybrid:
                        newSetFactory = HybridPointsToSet.getFactory();
                        break;
                    case SparkOptions.double_set_new_shared:
                        newSetFactory = SharedPointsToSet.getFactory();
                        break;
                    case SparkOptions.double_set_new_array:
                        newSetFactory = SortedArraySet.getFactory();
                        break;
                    case SparkOptions.double_set_new_bit:
                        newSetFactory = BitPointsToSet.getFactory();
                        break;
                    default:
                        throw new RuntimeException();
                }
                setFactory = DoublePointsToSet.getFactory( newSetFactory, oldSetFactory );
                break;
            default:
                throw new RuntimeException();
        }
    }

    public void solve() {
        config.solve();
        if( options.profile() ) {
            Jedd.v().outputProfile();
        }
    }


    private void buildQueues() {
        locals = qFactory.Qvar_method_type("locals");
        globals = qFactory.Qvar_type("globals");
        localallocs = qFactory.Qobj_method_type("localallocs");
        globalallocs = qFactory.Qobj_type("globalallocs");
    }

    private void build() {
        buildQueues();
        nodeManager = new NodeManager( locals, globals, localallocs, globalallocs );
        ni = factory.NodeInfo( locals.reader("mpc"), globals.reader("mpc"),
                localallocs.reader("mpc"), globalallocs.reader("mpc") );
        tm = factory.TypeManager(locals.reader("tm"), globals.reader("tm"),
                localallocs.reader("tm"), globalallocs.reader("tm"));
    }
}


